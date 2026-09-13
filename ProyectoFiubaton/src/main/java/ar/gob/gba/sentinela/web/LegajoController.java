package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.service.LegajoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/legajos")
public class LegajoController {

	private final LegajoService service;

	public LegajoController(LegajoService service) {
		this.service = service;
	}

	@GetMapping
	public List<NNyALegajo> listar(@RequestParam(required = false) String localidad) {
		return service.listar(localidad);
	}

	@GetMapping("/{idNnya}")
	public NNyALegajo obtener(@PathVariable String idNnya) {
		return service.obtener(idNnya);
	}

	@PostMapping
	public NNyALegajo guardar(@RequestBody NNyALegajo legajo) {
		return service.guardar(legajo);
	}
}
