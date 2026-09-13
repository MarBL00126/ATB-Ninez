package ar.gob.gba.sentinela.web;

import java.util.Map;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.orchestration.service.OrquestadorEvaluacionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluaciones")
public class EvaluacionController {

	private final OrquestadorEvaluacionService service;

	public EvaluacionController(OrquestadorEvaluacionService service) {
		this.service = service;
	}

	@PostMapping
	public AlertaRevision evaluar(@RequestBody EvaluacionRequest request) {
		return service.evaluar(request.idNnya(), request.features(), request.observacion());
	}

	public record EvaluacionRequest(String idNnya, Map<String, Object> features, String observacion) {
	}
}
