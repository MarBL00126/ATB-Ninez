package ar.gob.gba.sentinela.orchestration.client;

import ar.gob.gba.sentinela.orchestration.dto.MlPredictResponseDto;
import ar.gob.gba.sentinela.orchestration.dto.MlStructuredFeaturesDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MlPythonClient {

	private final RestClient restClient;

	public MlPythonClient(RestClient.Builder builder, @Value("${sentinela.services.ml-url}") String mlUrl) {
		this.restClient = builder.baseUrl(mlUrl).build();
	}

	public MlPredictResponseDto predict(MlStructuredFeaturesDto features) {
		return restClient.post()
				.uri("/predict")
				.body(features)
				.retrieve()
				.body(MlPredictResponseDto.class);
	}
}
