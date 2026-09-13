package ar.gob.gba.sentinela.vector.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import ar.gob.gba.sentinela.vector.dto.PaperIngestionRequest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class SeedPaperLoader {

	private static final String RESOURCE_PATTERN = "classpath*:/vector-papers/**/*.*";

	@Bean
	CommandLineRunner seedPapers(PaperVectorService paperVectorService, ResourcePatternResolver resourceResolver) {
		return args -> {
			Resource[] resources = resourceResolver.getResources(RESOURCE_PATTERN);
			for (Resource resource : resources) {
				if (!resource.isReadable() || resource.getFilename() == null || resource.getFilename().startsWith(".")
						|| resource.getFilename().equalsIgnoreCase("README.md")) {
					continue;
				}
				String source = "seed:" + resource.getFilename();
				if (paperVectorService.existsBySource(source)) {
					continue;
				}
				String text = extractText(resource);
				if (text.isBlank()) {
					continue;
				}
				String title = titleFromFilename(resource.getFilename());
				paperVectorService.ingest(new PaperIngestionRequest(title, "Seed predeploy", null, source, text));
			}
		};
	}

	private String extractText(Resource resource) throws IOException {
		String filename = resource.getFilename() == null ? "" : resource.getFilename().toLowerCase();
		try (InputStream inputStream = resource.getInputStream()) {
			if (filename.endsWith(".pdf")) {
				try (PDDocument document = PDDocument.load(inputStream)) {
					return new PDFTextStripper().getText(document);
				}
			}
			if (filename.endsWith(".txt") || filename.endsWith(".md")) {
				return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		}
		return "";
	}

	private String titleFromFilename(String filename) {
		int dot = filename.lastIndexOf('.');
		String base = dot > 0 ? filename.substring(0, dot) : filename;
		return base.replace('_', ' ').replace('-', ' ').trim();
	}
}
