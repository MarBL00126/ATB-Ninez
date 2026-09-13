package ar.gob.gba.sentinela.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.repository.NNyALegajoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class LegajoServiceTest {

	@Test
	void guardarPersisteLegajoYAlertaPendiente() {
		NNyALegajoRepository legajoRepository = mock(NNyALegajoRepository.class);
		AlertaRevisionRepository alertaRepository = mock(AlertaRevisionRepository.class);
		LegajoService service = new LegajoService(legajoRepository, alertaRepository);
		NNyALegajo legajo = legajo("NNYA-0005");
		when(legajoRepository.save(legajo)).thenReturn(legajo);
		when(alertaRepository.findFirstByIdNnyaAndEstadoInOrderByFechaCreacionDesc(any(), any()))
				.thenReturn(Optional.empty());

		service.guardar(legajo);

		verify(legajoRepository).save(legajo);
		ArgumentCaptor<AlertaRevision> captor = ArgumentCaptor.forClass(AlertaRevision.class);
		verify(alertaRepository).save(captor.capture());
		assertThat(captor.getValue().getIdNnya()).isEqualTo("NNYA-0005");
		assertThat(captor.getValue().getEstado()).isEqualTo(AlertaRevision.EstadoAlerta.PENDIENTE);
		assertThat(captor.getValue().getScoreRiesgo()).isGreaterThanOrEqualTo(0.70);
	}

	@Test
	void asegurarBandejaPendienteCreaAlertasParaLegajosExistentes() {
		NNyALegajoRepository legajoRepository = mock(NNyALegajoRepository.class);
		AlertaRevisionRepository alertaRepository = mock(AlertaRevisionRepository.class);
		LegajoService service = new LegajoService(legajoRepository, alertaRepository);
		when(alertaRepository.findFirstByIdNnyaAndEstadoInOrderByFechaCreacionDesc(any(), any()))
				.thenReturn(Optional.empty());

		service.asegurarBandejaPendiente(List.of(legajo("NNYA-0004"), legajo("NNYA-0005")));

		ArgumentCaptor<AlertaRevision> captor = ArgumentCaptor.forClass(AlertaRevision.class);
		verify(alertaRepository, org.mockito.Mockito.times(2)).save(captor.capture());
		assertThat(captor.getAllValues()).extracting(AlertaRevision::getIdNnya)
				.containsExactly("NNYA-0004", "NNYA-0005");
	}

	private NNyALegajo legajo(String idNnya) {
		NNyALegajo legajo = new NNyALegajo();
		legajo.setIdNnya(idNnya);
		legajo.setEdad((short) 8);
		legajo.setSexo("F");
		legajo.setLocalidadPartido("Isidro Casanova");
		legajo.setFechaAltaLegajo(LocalDate.now());
		return legajo;
	}
}
