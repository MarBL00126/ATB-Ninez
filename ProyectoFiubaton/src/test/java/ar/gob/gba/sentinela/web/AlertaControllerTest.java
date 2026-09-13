package ar.gob.gba.sentinela.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision.EstadoAlerta;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import ar.gob.gba.sentinela.security.jwt.JwtTokenProvider;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

class AlertaControllerTest {

	private final AlertaRevisionRepository repository = mock(AlertaRevisionRepository.class);
	private final LegajoService legajoService = mock(LegajoService.class);
	private final PermissionEvaluator permissionEvaluator = new PermissionEvaluator();
	private final JwtTokenProvider tokenProvider = new JwtTokenProvider();
	private final AlertaController controller = new AlertaController(repository, legajoService, permissionEvaluator);

	@Test
	void adminCanChangeAnyAlertStatus() {
		stubAlerta("NNYA-9001", "Quilmes");
		Authentication auth = tokenProvider.authenticate("Bearer demo:ADMIN,AUDITOR:Buenos Aires");

		AlertaRevision result = controller.cambiarEstado(10L, EstadoAlerta.EN_REVISION, auth);

		assertThat(result.getEstado()).isEqualTo(EstadoAlerta.EN_REVISION);
	}

	@Test
	void socialWorkerCanChangeAlertFromOwnMunicipality() {
		stubAlerta("NNYA-9002", "Isidro Casanova");
		Authentication auth = tokenProvider.authenticate("Bearer demo:TRABAJADOR_SOCIAL:Isidro Casanova");

		AlertaRevision result = controller.cambiarEstado(10L, EstadoAlerta.CONFIRMADA, auth);

		assertThat(result.getEstado()).isEqualTo(EstadoAlerta.CONFIRMADA);
	}

	@Test
	void socialWorkerCannotChangeAlertFromAnotherMunicipality() {
		stubAlerta("NNYA-9003", "Isidro Casanova");
		Authentication auth = tokenProvider.authenticate("Bearer demo:TRABAJADOR_SOCIAL:Quilmes");

		assertThatThrownBy(() -> controller.cambiarEstado(10L, EstadoAlerta.DESCARTADA, auth))
				.isInstanceOfSatisfying(ResponseStatusException.class,
						error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
	}

	private void stubAlerta(String idNnya, String localidad) {
		AlertaRevision alerta = new AlertaRevision();
		alerta.setId(10L);
		alerta.setIdNnya(idNnya);
		alerta.setScoreRiesgo(0.85);
		alerta.setExplicacion("Test; categorias=[ausentismo]");
		alerta.setEstado(EstadoAlerta.PENDIENTE);

		NNyALegajo legajo = new NNyALegajo();
		legajo.setIdNnya(idNnya);
		legajo.setLocalidadPartido(localidad);

		when(repository.findById(10L)).thenReturn(Optional.of(alerta));
		when(repository.save(any(AlertaRevision.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(legajoService.obtener(idNnya)).thenReturn(legajo);
	}
}
