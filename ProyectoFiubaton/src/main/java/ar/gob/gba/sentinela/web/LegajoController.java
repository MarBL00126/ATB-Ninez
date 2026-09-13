package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/legajos")
public class LegajoController {

	private final LegajoService service;
	private final PermissionEvaluator permissionEvaluator;

	public LegajoController(LegajoService service, PermissionEvaluator permissionEvaluator) {
		this.service = service;
		this.permissionEvaluator = permissionEvaluator;
	}

	@GetMapping
	public List<NNyALegajo> listar(@RequestParam(required = false) String localidad, Authentication authentication) {
		return service.listar(resolveLocalidad(localidad, authentication));
	}

	@GetMapping("/{idNnya}")
	public NNyALegajo obtener(@PathVariable String idNnya, Authentication authentication) {
		NNyALegajo legajo = service.obtener(idNnya);
		requireAccess(authentication, legajo.getLocalidadPartido());
		return legajo;
	}

	@PostMapping
	public NNyALegajo guardar(@RequestBody NNyALegajo legajo, Authentication authentication) {
		if (!permissionEvaluator.hasGlobalAccess(authentication)) {
			String scopedLocalidad = requireScopedLocalidad(authentication);
			if (legajo.getLocalidadPartido() == null || legajo.getLocalidadPartido().isBlank()) {
				legajo.setLocalidadPartido(scopedLocalidad);
			}
			requireAccess(authentication, legajo.getLocalidadPartido());
		}
		return service.guardar(legajo);
	}

	private String resolveLocalidad(String requestedLocalidad, Authentication authentication) {
		if (permissionEvaluator.hasGlobalAccess(authentication)) {
			return requestedLocalidad;
		}
		String scopedLocalidad = requireScopedLocalidad(authentication);
		if (requestedLocalidad != null && !requestedLocalidad.isBlank()
				&& !requestedLocalidad.equalsIgnoreCase(scopedLocalidad)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		return scopedLocalidad;
	}

	private void requireAccess(Authentication authentication, String localidad) {
		if (!permissionEvaluator.canAccessLocalidad(authentication, localidad)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	private String requireScopedLocalidad(Authentication authentication) {
		String scopedLocalidad = permissionEvaluator.scopedLocalidad(authentication);
		if (scopedLocalidad == null || scopedLocalidad.isBlank()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		return scopedLocalidad;
	}
}
