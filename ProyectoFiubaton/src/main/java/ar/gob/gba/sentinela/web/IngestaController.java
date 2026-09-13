package ar.gob.gba.sentinela.web;

import ar.gob.gba.sentinela.domain.entity.ClubesColoniasReg;
import ar.gob.gba.sentinela.domain.entity.DesarrolloSocialReg;
import ar.gob.gba.sentinela.domain.entity.EducacionRegistro;
import ar.gob.gba.sentinela.domain.entity.JusticiaRegistro;
import ar.gob.gba.sentinela.domain.entity.Linea102Registro;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.entity.SaludRegistro;
import ar.gob.gba.sentinela.domain.service.CargaFuentesService;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/ingesta")
public class IngestaController {

	private final CargaFuentesService service;
	private final LegajoService legajoService;
	private final PermissionEvaluator permissionEvaluator;

	public IngestaController(CargaFuentesService service, LegajoService legajoService,
			PermissionEvaluator permissionEvaluator) {
		this.service = service;
		this.legajoService = legajoService;
		this.permissionEvaluator = permissionEvaluator;
	}

	@PostMapping("/educacion")
	public EducacionRegistro educacion(@RequestBody EducacionRegistro registro, Authentication authentication) {
		requireLegajoAccess(registro.getIdNnya(), authentication);
		return service.guardarEducacion(registro);
	}

	@PostMapping("/salud")
	public SaludRegistro salud(@RequestBody SaludRegistro registro, Authentication authentication) {
		requireLegajoAccess(registro.getIdNnya(), authentication);
		return service.guardarSalud(registro);
	}

	@PostMapping("/linea-102")
	public Linea102Registro linea102(@RequestBody Linea102Registro registro, Authentication authentication) {
		requireLegajoAccess(registro.getIdNnya(), authentication);
		return service.guardarLinea102(registro);
	}

	@PostMapping("/desarrollo-social")
	public DesarrolloSocialReg desarrolloSocial(@RequestBody DesarrolloSocialReg registro,
			Authentication authentication) {
		requireLegajoAccess(registro.getIdNnya(), authentication);
		return service.guardarDesarrolloSocial(registro);
	}

	@PostMapping("/justicia")
	public JusticiaRegistro justicia(@RequestBody JusticiaRegistro registro, Authentication authentication) {
		requireLegajoAccess(registro.getIdNnya(), authentication);
		return service.guardarJusticia(registro);
	}

	@PostMapping("/clubes-colonias")
	public ClubesColoniasReg clubes(@RequestBody ClubesColoniasReg registro, Authentication authentication) {
		requireLegajoAccess(registro.getIdNnya(), authentication);
		return service.guardarClubes(registro);
	}

	private void requireLegajoAccess(String idNnya, Authentication authentication) {
		NNyALegajo legajo = legajoService.obtener(idNnya);
		if (!permissionEvaluator.canAccessLocalidad(authentication, legajo.getLocalidadPartido())) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}
}
