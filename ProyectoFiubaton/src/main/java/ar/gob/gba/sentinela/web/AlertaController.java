package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision.EstadoAlerta;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/alertas")
public class AlertaController {

	private final AlertaRevisionRepository repository;
	private final LegajoService legajoService;
	private final PermissionEvaluator permissionEvaluator;

	public AlertaController(AlertaRevisionRepository repository, LegajoService legajoService,
			PermissionEvaluator permissionEvaluator) {
		this.repository = repository;
		this.legajoService = legajoService;
		this.permissionEvaluator = permissionEvaluator;
	}

	@GetMapping
	public List<AlertaRevision> listar(@RequestParam(defaultValue = "PENDIENTE") EstadoAlerta estado,
			Authentication authentication) {
		if (!permissionEvaluator.hasGlobalAccess(authentication)) {
			String scopedLocalidad = requireScopedLocalidad(authentication);
			List<NNyALegajo> legajos = legajoService.listar(scopedLocalidad);
			List<String> idsNnya = legajos.stream()
					.map(NNyALegajo::getIdNnya)
					.toList();
			return idsNnya.isEmpty()
					? List.of()
					: repository.findByEstadoAndIdNnyaInOrderByFechaCreacionDesc(estado, idsNnya);
		}
		return repository.findByEstadoOrderByFechaCreacionDesc(estado);
	}

	@PatchMapping("/{id}/estado")
	public AlertaRevision cambiarEstado(@PathVariable Long id, @RequestParam EstadoAlerta estado,
			Authentication authentication) {
		AlertaRevision alerta = repository.findById(id).orElseThrow();
		NNyALegajo legajo = legajoService.obtener(alerta.getIdNnya());
		if (!canAccessLocalidad(authentication, legajo.getLocalidadPartido())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		alerta.setEstado(estado);
		return repository.save(alerta);
	}

	private boolean canAccessLocalidad(Authentication authentication, String localidad) {
		if (permissionEvaluator.hasGlobalAccess(authentication)) {
			return true;
		}
		return normalize(requireScopedLocalidad(authentication)).equalsIgnoreCase(normalize(localidad));
	}

	private String requireScopedLocalidad(Authentication authentication) {
		String scopedLocalidad = permissionEvaluator.scopedLocalidad(authentication);
		if (scopedLocalidad == null || scopedLocalidad.isBlank()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		return scopedLocalidad;
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim();
	}
}
