package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision.EstadoAlerta;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/alertas")
public class AlertaController {

	private final AlertaRevisionRepository repository;

	public AlertaController(AlertaRevisionRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<AlertaRevision> listar(@RequestParam(defaultValue = "PENDIENTE") EstadoAlerta estado) {
		return repository.findByEstadoOrderByFechaCreacionDesc(estado);
	}

	@PatchMapping("/{id}/estado")
	public AlertaRevision cambiarEstado(@PathVariable Long id, @RequestParam EstadoAlerta estado) {
		AlertaRevision alerta = repository.findById(id).orElseThrow();
		alerta.setEstado(estado);
		return repository.save(alerta);
	}
}
