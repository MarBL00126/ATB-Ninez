package ar.gob.gba.sentinela.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.NNyALegajoRepository;
import org.junit.jupiter.api.Test;

class LegajoServiceTest {

	@Test
	void guardarPersisteLegajoSinInventarScoreDeAlerta() {
		NNyALegajoRepository legajoRepository = mock(NNyALegajoRepository.class);
		LegajoService service = new LegajoService(legajoRepository);
		NNyALegajo legajo = legajo("NNYA-0005");
		when(legajoRepository.save(legajo)).thenReturn(legajo);

		NNyALegajo saved = service.guardar(legajo);

		verify(legajoRepository).save(legajo);
		assertThat(saved).isSameAs(legajo);
	}

	@Test
	void listarFiltraPorLocalidad() {
		NNyALegajoRepository legajoRepository = mock(NNyALegajoRepository.class);
		LegajoService service = new LegajoService(legajoRepository);
		NNyALegajo legajo = legajo("NNYA-0004");
		when(legajoRepository.findByLocalidadPartidoIgnoreCase("Isidro Casanova")).thenReturn(List.of(legajo));

		List<NNyALegajo> result = service.listar("Isidro Casanova");

		assertThat(result).containsExactly(legajo);
		verify(legajoRepository).findByLocalidadPartidoIgnoreCase("Isidro Casanova");
	}

	@Test
	void listarSinLocalidadTraeTodos() {
		NNyALegajoRepository legajoRepository = mock(NNyALegajoRepository.class);
		LegajoService service = new LegajoService(legajoRepository);
		NNyALegajo legajo = legajo("NNYA-0004");
		when(legajoRepository.findAll()).thenReturn(List.of(legajo));

		List<NNyALegajo> result = service.listar(null);

		assertThat(result).containsExactly(legajo);
		verify(legajoRepository).findAll();
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
