package ar.gob.gba.sentinela.orchestration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.orchestration.client.MlPythonClient;
import ar.gob.gba.sentinela.orchestration.client.NlpChromaClient;
import ar.gob.gba.sentinela.orchestration.dto.MlPredictResponseDto;
import ar.gob.gba.sentinela.orchestration.dto.MlStructuredFeaturesDto;
import ar.gob.gba.sentinela.orchestration.dto.NlpSemanticQueryDto;
import ar.gob.gba.sentinela.orchestration.dto.NlpSemanticResponseDto;
import ar.gob.gba.sentinela.orchestration.service.OrquestadorEvaluacionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class OrquestadorTest {

	@Test
	void highRiskPredictionCreatesPendingAlert() {
		MlPythonClient ml = mock(MlPythonClient.class);
		NlpChromaClient nlp = mock(NlpChromaClient.class);
		AlertaRevisionRepository repository = mock(AlertaRevisionRepository.class);
		OrquestadorEvaluacionService service = new OrquestadorEvaluacionService(ml, nlp, repository, true);

		when(ml.predict(ArgumentMatchers.any(MlStructuredFeaturesDto.class)))
				.thenReturn(new MlPredictResponseDto(0.91, Map.of("ausentismo", 0.4)));
		when(nlp.embeddings(ArgumentMatchers.any(NlpSemanticQueryDto.class)))
				.thenReturn(new NlpSemanticResponseDto(0.82, List.of("violencia")));
		when(repository.save(ArgumentMatchers.any(AlertaRevision.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		AlertaRevision alerta = service.evaluar("NNYA-0001", Map.of("ausentismo", 12), "observacion");

		assertThat(alerta.getEstado()).isEqualTo(AlertaRevision.EstadoAlerta.PENDIENTE);
		assertThat(alerta.getExplicacion()).contains("ausentismo");
	}
}
