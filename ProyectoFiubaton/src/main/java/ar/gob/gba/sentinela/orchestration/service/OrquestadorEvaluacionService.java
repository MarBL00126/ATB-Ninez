package ar.gob.gba.sentinela.orchestration.service;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ar.gob.gba.sentinela.audit.aspect.AuditedAction;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.orchestration.client.MlPythonClient;
import ar.gob.gba.sentinela.orchestration.client.NlpChromaClient;
import ar.gob.gba.sentinela.orchestration.dto.MlPredictResponseDto;
import ar.gob.gba.sentinela.orchestration.dto.MlStructuredFeaturesDto;
import ar.gob.gba.sentinela.orchestration.dto.NlpSemanticQueryDto;
import ar.gob.gba.sentinela.orchestration.dto.NlpSemanticResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

@Service
public class OrquestadorEvaluacionService {

	private static final double UMBRAL_ALERTA = 0.70;

	private final MlPythonClient mlPythonClient;
	private final NlpChromaClient nlpChromaClient;
	private final AlertaRevisionRepository alertaRepository;
	private final boolean externalAiEnabled;

	public OrquestadorEvaluacionService(MlPythonClient mlPythonClient, NlpChromaClient nlpChromaClient,
			AlertaRevisionRepository alertaRepository,
			@Value("${sentinela.services.external-ai-enabled:false}") boolean externalAiEnabled) {
		this.mlPythonClient = mlPythonClient;
		this.nlpChromaClient = nlpChromaClient;
		this.alertaRepository = alertaRepository;
		this.externalAiEnabled = externalAiEnabled;
	}

	@AuditedAction("EVALUACION_ORQUESTADA")
	@Transactional
	public AlertaRevision evaluar(String idNnya, Map<String, Object> features, String observacion) {
		MlPredictResponseDto ml = predict(idNnya, features);
		NlpSemanticResponseDto nlp = analyze(idNnya, observacion);

		AlertaRevision alerta = alertaRepository
				.findFirstByIdNnyaAndEstadoInOrderByFechaCreacionDesc(idNnya,
						List.of(AlertaRevision.EstadoAlerta.PENDIENTE, AlertaRevision.EstadoAlerta.EN_REVISION))
				.orElseGet(AlertaRevision::new);
		alerta.setIdNnya(idNnya);
		alerta.setScoreRiesgo(ml.scoreRiesgo());
		alerta.setExplicacion(explicar(ml, nlp));
		if (ml.scoreRiesgo() < UMBRAL_ALERTA) {
			alerta.setEstado(AlertaRevision.EstadoAlerta.DESCARTADA);
		}
		return alertaRepository.save(alerta);
	}

	private MlPredictResponseDto predict(String idNnya, Map<String, Object> features) {
		if (externalAiEnabled) {
			try {
				return mlPythonClient.predict(new MlStructuredFeaturesDto(idNnya, features));
			}
			catch (RestClientException ex) {
				// Railway single-service mode falls back to deterministic local scoring.
			}
		}
		Map<String, Double> values = Map.of(
				"ausentismo", normalizedNumber(features.get("ausentismo_dias_ultimo_mes"), 20) * 0.14,
				"llamados_102", Math.min(number(features.get("cantidad_llamados_previos_linea102")), 3) * 0.10,
				"guardia", normalizedNumber(features.get("consultas_guardia_lesiones_pococlaras_ult12m"), 4) * 0.14,
				"intervencion_reciente", number(features.get("meses_desde_ultima_intervencion")) <= 6 ? 0.08 : 0.0,
				"desercion", bool(features.get("desercion_o_abandono_intermitente")) ? 0.12 : 0.0,
				"lesiones_docentes", bool(features.get("lesiones_reportadas_por_docentes")) ? 0.16 : 0.0,
				"autolesion_consumo", bool(features.get("atencion_por_autolesion_o_consumo")) ? 0.16 : 0.0,
				"denuncia_vd_hogar", bool(features.get("denuncia_violencia_domestica_en_el_hogar")) ? 0.16 : 0.0);
		double score = values.values().stream().mapToDouble(Double::doubleValue).sum();
		return new MlPredictResponseDto(Math.min(score, 1.0), values);
	}

	private NlpSemanticResponseDto analyze(String idNnya, String observacion) {
		if (externalAiEnabled) {
			try {
				return nlpChromaClient.embeddings(new NlpSemanticQueryDto(idNnya, observacion));
			}
			catch (RestClientException ex) {
				// Railway single-service mode falls back to keyword categories.
			}
		}
		String text = observacion == null ? "" : observacion.toLowerCase();
		List<String> categories = new ArrayList<>();
		if (containsAny(text, "aislamiento", "aislad", "solo")) {
			categories.add("aislamiento");
		}
		if (containsAny(text, "golpe", "lesion", "moreton", "violencia")) {
			categories.add("violencia_fisica");
		}
		if (containsAny(text, "ausencia", "ausent", "faltas")) {
			categories.add("ausentismo");
		}
		if (containsAny(text, "hambre", "higiene", "abandono", "negligencia")) {
			categories.add("negligencia");
		}
		if (categories.isEmpty()) {
			categories.add("sin_categoria_clara");
		}
		return new NlpSemanticResponseDto(0.0, categories);
	}

	private String explicar(MlPredictResponseDto ml, NlpSemanticResponseDto nlp) {
		String shap = ml.shapValues() == null ? "" : ml.shapValues().entrySet().stream()
				.sorted(Map.Entry.<String, Double>comparingByValue().reversed())
				.limit(5)
				.map(entry -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining(", "));
		String categorias = nlp.categoriasDetectadas() == null ? "" : String.join(", ", nlp.categoriasDetectadas());
		return "score=" + ml.scoreRiesgo() + "; shap=[" + shap + "]; similitud="
				+ nlp.similitudLongitudinal() + "; categorias=[" + categorias + "]";
	}

	private double normalizedNumber(Object value, double denominator) {
		return Math.min(number(value) / denominator, 1.0);
	}

	private double number(Object value) {
		if (value instanceof Number number) {
			return number.doubleValue();
		}
		if (value == null) {
			return 0.0;
		}
		try {
			return Double.parseDouble(value.toString());
		}
		catch (NumberFormatException ex) {
			return 0.0;
		}
	}

	private boolean bool(Object value) {
		if (value instanceof Boolean bool) {
			return bool;
		}
		return value != null && Boolean.parseBoolean(value.toString());
	}

	private boolean containsAny(String text, String... words) {
		for (String word : words) {
			if (text.contains(word)) {
				return true;
			}
		}
		return false;
	}
}
