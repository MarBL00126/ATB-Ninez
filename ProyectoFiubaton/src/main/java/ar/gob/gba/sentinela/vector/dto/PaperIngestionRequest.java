package ar.gob.gba.sentinela.vector.dto;

import jakarta.validation.constraints.NotBlank;

public record PaperIngestionRequest(
		@NotBlank String title,
		String authors,
		Integer year,
		String source,
		@NotBlank String text) {
}
