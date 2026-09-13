package ar.gob.gba.sentinela.vector.dto;

public record PaperSearchResult(
		Long id,
		String title,
		String authors,
		Integer year,
		String source,
		String content,
		double score) {
}
