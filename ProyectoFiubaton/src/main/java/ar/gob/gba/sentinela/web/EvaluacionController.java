package ar.gob.gba.sentinela.web;

import java.util.Map;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import ar.gob.gba.sentinela.orchestration.service.OrquestadorEvaluacionService;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/evaluaciones")
public class EvaluacionController {

	private final OrquestadorEvaluacionService service;
	private final LegajoService legajoService;
	private final PermissionEvaluator permissionEvaluator;

	public EvaluacionController(OrquestadorEvaluacionService service, LegajoService legajoService,
			PermissionEvaluator permissionEvaluator) {
		this.service = service;
		this.legajoService = legajoService;
		this.permissionEvaluator = permissionEvaluator;
	}

	@PostMapping
	public AlertaRevision evaluar(@RequestBody EvaluacionRequest request, Authentication authentication) {
		NNyALegajo legajo = legajoService.obtener(request.idNnya());
		if (!permissionEvaluator.canAccessLocalidad(authentication, legajo.getLocalidadPartido())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		return service.evaluar(request.idNnya(), request.features(), request.observacion());
	}

	public record EvaluacionRequest(String idNnya, Map<String, Object> features, String observacion) {
	}
}
