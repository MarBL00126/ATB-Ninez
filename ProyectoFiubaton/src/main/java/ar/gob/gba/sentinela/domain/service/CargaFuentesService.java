package ar.gob.gba.sentinela.domain.service;

import java.util.List;

import ar.gob.gba.sentinela.audit.aspect.AuditedAction;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.ClubesColoniasReg;
import ar.gob.gba.sentinela.domain.entity.DesarrolloSocialReg;
import ar.gob.gba.sentinela.domain.entity.EducacionRegistro;
import ar.gob.gba.sentinela.domain.entity.JusticiaRegistro;
import ar.gob.gba.sentinela.domain.entity.Linea102Registro;
import ar.gob.gba.sentinela.domain.entity.SaludRegistro;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.repository.ClubesColoniasRegRepository;
import ar.gob.gba.sentinela.domain.repository.DesarrolloSocialRegRepository;
import ar.gob.gba.sentinela.domain.repository.EducacionRegistroRepository;
import ar.gob.gba.sentinela.domain.repository.JusticiaRegistroRepository;
import ar.gob.gba.sentinela.domain.repository.Linea102RegistroRepository;
import ar.gob.gba.sentinela.domain.repository.SaludRegistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CargaFuentesService {

	private final EducacionRegistroRepository educacionRepository;
	private final SaludRegistroRepository saludRepository;
	private final Linea102RegistroRepository linea102Repository;
	private final DesarrolloSocialRegRepository desarrolloSocialRepository;
	private final JusticiaRegistroRepository justiciaRepository;
	private final ClubesColoniasRegRepository clubesRepository;
	private final AlertaRevisionRepository alertaRepository;

	public CargaFuentesService(EducacionRegistroRepository educacionRepository,
			SaludRegistroRepository saludRepository,
			Linea102RegistroRepository linea102Repository,
			DesarrolloSocialRegRepository desarrolloSocialRepository,
			JusticiaRegistroRepository justiciaRepository,
			ClubesColoniasRegRepository clubesRepository,
			AlertaRevisionRepository alertaRepository) {
		this.educacionRepository = educacionRepository;
		this.saludRepository = saludRepository;
		this.linea102Repository = linea102Repository;
		this.desarrolloSocialRepository = desarrolloSocialRepository;
		this.justiciaRepository = justiciaRepository;
		this.clubesRepository = clubesRepository;
		this.alertaRepository = alertaRepository;
	}

	@AuditedAction("INGESTA_EDUCACION")
	@Transactional
	public EducacionRegistro guardarEducacion(EducacionRegistro registro) {
		EducacionRegistro saved = educacionRepository.save(registro);
		asegurarAlertaPendiente(saved.getIdNnya(), scoreEducacion(saved), explicacion("educacion"));
		return saved;
	}

	@Transactional
	public SaludRegistro guardarSalud(SaludRegistro registro) {
		SaludRegistro saved = saludRepository.save(registro);
		asegurarAlertaPendiente(saved.getIdNnya(), scoreSalud(saved), explicacion("salud"));
		return saved;
	}

	@Transactional
	public Linea102Registro guardarLinea102(Linea102Registro registro) {
		Linea102Registro saved = linea102Repository.save(registro);
		asegurarAlertaPendiente(saved.getIdNnya(), scoreLinea102(saved), explicacion("linea_102_137"));
		return saved;
	}

	@Transactional
	public DesarrolloSocialReg guardarDesarrolloSocial(DesarrolloSocialReg registro) {
		DesarrolloSocialReg saved = desarrolloSocialRepository.save(registro);
		asegurarAlertaPendiente(saved.getIdNnya(), scoreDesarrolloSocial(saved), explicacion("desarrollo_social"));
		return saved;
	}

	@Transactional
	public JusticiaRegistro guardarJusticia(JusticiaRegistro registro) {
		JusticiaRegistro saved = justiciaRepository.save(registro);
		asegurarAlertaPendiente(saved.getIdNnya(), scoreJusticia(saved), explicacion("justicia_seguridad"));
		return saved;
	}

	@Transactional
	public ClubesColoniasReg guardarClubes(ClubesColoniasReg registro) {
		ClubesColoniasReg saved = clubesRepository.save(registro);
		asegurarAlertaPendiente(saved.getIdNnya(), scoreClubes(saved), explicacion("clubes_colonias"));
		return saved;
	}

	private void asegurarAlertaPendiente(String idNnya, Double score, String explicacion) {
		AlertaRevision alerta = alertaRepository
				.findFirstByIdNnyaAndEstadoInOrderByFechaCreacionDesc(idNnya,
						List.of(AlertaRevision.EstadoAlerta.PENDIENTE, AlertaRevision.EstadoAlerta.EN_REVISION))
				.orElseGet(AlertaRevision::new);
		alerta.setIdNnya(idNnya);
		alerta.setScoreRiesgo(Math.max(score, alerta.getScoreRiesgo() == null ? 0.0 : alerta.getScoreRiesgo()));
		alerta.setExplicacion(explicacion);
		alerta.setEstado(AlertaRevision.EstadoAlerta.PENDIENTE);
		alertaRepository.save(alerta);
	}

	private double scoreEducacion(EducacionRegistro registro) {
		double score = 0.55;
		score += normalized(registro.getAusentismoDiasUltimoMes(), 20) * 0.20;
		if (truthy(registro.getDesercionOAbandonoIntermitente())) score += 0.12;
		if (truthy(registro.getCaidaRendimientoSubita())) score += 0.08;
		if (truthy(registro.getLesionesReportadasPorDocentes())) score += 0.18;
		return bounded(score);
	}

	private double scoreSalud(SaludRegistro registro) {
		double score = 0.55;
		if (truthy(registro.getControlesPediatricosAtrasados())) score += 0.08;
		score += normalized(registro.getConsultasGuardiaLesionesPocoClarasUlt12m(), 4) * 0.18;
		if (truthy(registro.getBajoPesoORetrasoDesarrollo())) score += 0.08;
		if (truthy(registro.getAtencionPorAutolesionOConsumo())) score += 0.18;
		return bounded(score);
	}

	private double scoreLinea102(Linea102Registro registro) {
		double score = 0.58;
		score += Math.min(number(registro.getCantidadLlamadosPreviosLinea102()), 3) * 0.07;
		if (truthy(registro.getLegajoPrevioEnRunna())) score += 0.08;
		if (number(registro.getMesesDesdeUltimaIntervencion()) <= 3) score += 0.08;
		if ("ALTA".equalsIgnoreCase(registro.getUrgenciaPercibida())
				|| "DERIVACION_911".equalsIgnoreCase(registro.getUrgenciaPercibida())) {
			score += 0.12;
		}
		return bounded(score);
	}

	private double scoreDesarrolloSocial(DesarrolloSocialReg registro) {
		double score = 0.55;
		if (truthy(registro.getAuhUOtraPrestacionInterrumpida())) score += 0.12;
		String vivienda = registro.getSituacionHabitacionalReportada();
		if (vivienda != null && !vivienda.equalsIgnoreCase("Adecuada")) score += 0.10;
		return bounded(score);
	}

	private double scoreJusticia(JusticiaRegistro registro) {
		double score = 0.60;
		if (truthy(registro.getDenunciaViolenciaDomesticaEnElHogar())) score += 0.18;
		if (truthy(registro.getAntecedentesPenalesConvivientesViolenciaAbuso())) score += 0.18;
		return bounded(score);
	}

	private double scoreClubes(ClubesColoniasReg registro) {
		double score = 0.52;
		if (truthy(registro.getAusenciasReiteradasSinAviso())) score += 0.10;
		if (truthy(registro.getObservacionesInformalesProfesorMonitor())) score += 0.12;
		return bounded(score);
	}

	private String explicacion(String categoria) {
		return "Dato persistido en BDD; categorias=[" + categoria + ", evaluacion_pendiente]";
	}

	private boolean truthy(Boolean value) {
		return Boolean.TRUE.equals(value);
	}

	private double normalized(Number value, double denominator) {
		return Math.min(number(value) / denominator, 1.0);
	}

	private double number(Number value) {
		return value == null ? 0.0 : value.doubleValue();
	}

	private double bounded(double value) {
		return Math.max(0.0, Math.min(value, 1.0));
	}
}
