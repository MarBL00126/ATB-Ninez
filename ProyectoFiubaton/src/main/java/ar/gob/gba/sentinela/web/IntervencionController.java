package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.Intervencion;
import ar.gob.gba.sentinela.domain.service.IntervencionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/intervenciones")
public class IntervencionController {

	private final IntervencionService service;

	public IntervencionController(IntervencionService service) {
		this.service = service;
	}

	@GetMapping("/{idNnya}")
	public List<Intervencion> listar(@PathVariable String idNnya) {
		return service.listarPorLegajo(idNnya);
	}

	@PostMapping("/{idNnya}")
	public Intervencion registrar(@PathVariable String idNnya, @RequestBody Intervencion intervencion) {
		return service.registrar(idNnya, intervencion);
	}
}
