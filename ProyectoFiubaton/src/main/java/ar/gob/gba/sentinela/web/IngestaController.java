package ar.gob.gba.sentinela.web;

import ar.gob.gba.sentinela.domain.entity.ClubesColoniasReg;
import ar.gob.gba.sentinela.domain.entity.DesarrolloSocialReg;
import ar.gob.gba.sentinela.domain.entity.EducacionRegistro;
import ar.gob.gba.sentinela.domain.entity.JusticiaRegistro;
import ar.gob.gba.sentinela.domain.entity.Linea102Registro;
import ar.gob.gba.sentinela.domain.entity.SaludRegistro;
import ar.gob.gba.sentinela.domain.service.CargaFuentesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ingesta")
public class IngestaController {

	private final CargaFuentesService service;

	public IngestaController(CargaFuentesService service) {
		this.service = service;
	}

	@PostMapping("/educacion")
	public EducacionRegistro educacion(@RequestBody EducacionRegistro registro) {
		return service.guardarEducacion(registro);
	}

	@PostMapping("/salud")
	public SaludRegistro salud(@RequestBody SaludRegistro registro) {
		return service.guardarSalud(registro);
	}

	@PostMapping("/linea-102")
	public Linea102Registro linea102(@RequestBody Linea102Registro registro) {
		return service.guardarLinea102(registro);
	}

	@PostMapping("/desarrollo-social")
	public DesarrolloSocialReg desarrolloSocial(@RequestBody DesarrolloSocialReg registro) {
		return service.guardarDesarrolloSocial(registro);
	}

	@PostMapping("/justicia")
	public JusticiaRegistro justicia(@RequestBody JusticiaRegistro registro) {
		return service.guardarJusticia(registro);
	}

	@PostMapping("/clubes-colonias")
	public ClubesColoniasReg clubes(@RequestBody ClubesColoniasReg registro) {
		return service.guardarClubes(registro);
	}
}
