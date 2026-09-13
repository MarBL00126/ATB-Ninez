package ar.gob.gba.sentinela.identity.client;

import ar.gob.gba.sentinela.identity.dto.ReidentificacionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class VaultHttpClient {

	private final RestClient restClient;

	public VaultHttpClient(RestClient.Builder builder,
			@Value("${sentinela.services.vault-url:http://localhost:8200}") String vaultUrl) {
		this.restClient = builder.baseUrl(vaultUrl).build();
	}

	public String reidentificar(ReidentificacionRequest request) {
		return restClient.post()
				.uri("/identity/reidentificar")
				.body(request)
				.retrieve()
				.body(String.class);
	}
}
