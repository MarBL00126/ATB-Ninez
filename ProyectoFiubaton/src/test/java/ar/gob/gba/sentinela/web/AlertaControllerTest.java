package ar.gob.gba.sentinela.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision.EstadoAlerta;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.repository.NNyALegajoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AlertaControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private NNyALegajoRepository legajoRepository;

	@Autowired
	private AlertaRevisionRepository alertaRepository;

	@BeforeEach
	void clean() {
		alertaRepository.deleteAll();
		legajoRepository.deleteAll();
	}

	@Test
	void adminCanChangeAnyAlertStatus() throws Exception {
		AlertaRevision alerta = crearAlerta("NNYA-9001", "Quilmes");

		mockMvc.perform(patch("/api/v1/alertas/{id}/estado", alerta.getId())
						.param("estado", "EN_REVISION")
						.header("Authorization", "Bearer demo:ADMIN,AUDITOR:Buenos Aires"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("EN_REVISION"));
	}

	@Test
	void socialWorkerCanChangeAlertFromOwnMunicipality() throws Exception {
		AlertaRevision alerta = crearAlerta("NNYA-9002", "Isidro Casanova");

		mockMvc.perform(patch("/api/v1/alertas/{id}/estado", alerta.getId())
						.param("estado", "CONFIRMADA")
						.header("Authorization", "Bearer demo:TRABAJADOR_SOCIAL:Isidro Casanova"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("CONFIRMADA"));
	}

	@Test
	void socialWorkerCannotChangeAlertFromAnotherMunicipality() throws Exception {
		AlertaRevision alerta = crearAlerta("NNYA-9003", "Isidro Casanova");

		mockMvc.perform(patch("/api/v1/alertas/{id}/estado", alerta.getId())
						.param("estado", "DESCARTADA")
						.header("Authorization", "Bearer demo:TRABAJADOR_SOCIAL:Quilmes"))
				.andExpect(status().isForbidden());
	}

	private AlertaRevision crearAlerta(String idNnya, String localidad) {
		NNyALegajo legajo = new NNyALegajo();
		legajo.setIdNnya(idNnya);
		legajo.setEdad((short) 8);
		legajo.setSexo("F");
		legajo.setLocalidadPartido(localidad);
		legajo.setFechaAltaLegajo(LocalDate.now());
		legajoRepository.saveAndFlush(legajo);

		AlertaRevision alerta = new AlertaRevision();
		alerta.setIdNnya(idNnya);
		alerta.setScoreRiesgo(0.85);
		alerta.setExplicacion("Test; categorias=[ausentismo]");
		alerta.setEstado(EstadoAlerta.PENDIENTE);
		return alertaRepository.saveAndFlush(alerta);
	}
}
