package ar.gob.gba.sentinela.web;

import java.util.List;

import ar.gob.gba.sentinela.vector.dto.PaperIngestionRequest;
import ar.gob.gba.sentinela.vector.dto.PaperIngestionResponse;
import ar.gob.gba.sentinela.vector.dto.PaperSearchRequest;
import ar.gob.gba.sentinela.vector.dto.PaperSearchResult;
import ar.gob.gba.sentinela.vector.service.PaperVectorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/biblioteca")
public class PaperLibraryController {

	private final PaperVectorService service;

	public PaperLibraryController(PaperVectorService service) {
		this.service = service;
	}

	@GetMapping("/health")
	public VectorHealth health() {
		return new VectorHealth(service.isVectorEnabled());
	}

	@PostMapping("/papers")
	public PaperIngestionResponse ingest(@Valid @RequestBody PaperIngestionRequest request) {
		return service.ingest(request);
	}

	@PostMapping("/search")
	public List<PaperSearchResult> search(@Valid @RequestBody PaperSearchRequest request) {
		return service.search(request);
	}

	public record VectorHealth(boolean vectorEnabled) {
	}
}
