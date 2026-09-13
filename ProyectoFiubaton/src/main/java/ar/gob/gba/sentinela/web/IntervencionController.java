package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.Intervencion;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.service.IntervencionService;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/intervenciones")
public class IntervencionController {

	private final IntervencionService service;
	private final LegajoService legajoService;
	private final PermissionEvaluator permissionEvaluator;

	public IntervencionController(IntervencionService service, LegajoService legajoService,
			PermissionEvaluator permissionEvaluator) {
		this.service = service;
		this.legajoService = legajoService;
		this.permissionEvaluator = permissionEvaluator;
	}

	@GetMapping("/{idNnya}")
	public List<Intervencion> listar(@PathVariable String idNnya, Authentication authentication) {
		requireLegajoAccess(idNnya, authentication);
		return service.listarPorLegajo(idNnya);
	}

	@PostMapping("/{idNnya}")
	public Intervencion registrar(@PathVariable String idNnya, @RequestBody Intervencion intervencion,
			Authentication authentication) {
		requireLegajoAccess(idNnya, authentication);
		return service.registrar(idNnya, intervencion);
	}

	private void requireLegajoAccess(String idNnya, Authentication authentication) {
		NNyALegajo legajo = legajoService.obtener(idNnya);
		if (!permissionEvaluator.canAccessLocalidad(authentication, legajo.getLocalidadPartido())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}
}
