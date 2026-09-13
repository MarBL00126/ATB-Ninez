package ar.gob.gba.sentinela.orchestration.client;

import ar.gob.gba.sentinela.orchestration.dto.NlpSemanticQueryDto;
import ar.gob.gba.sentinela.orchestration.dto.NlpSemanticResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NlpChromaClient {

	private final RestClient restClient;

	public NlpChromaClient(RestClient.Builder builder, @Value("${sentinela.services.nlp-url}") String nlpUrl) {
		this.restClient = builder.baseUrl(nlpUrl).build();
	}

	public NlpSemanticResponseDto embeddings(NlpSemanticQueryDto query) {
		return restClient.post()
				.uri("/embeddings")
				.body(query)
				.retrieve()
				.body(NlpSemanticResponseDto.class);
	}
}
