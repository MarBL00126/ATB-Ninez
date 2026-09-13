package ar.gob.gba.sentinela.orchestration.dto;

import java.util.Map;

public record MlStructuredFeaturesDto(String idNnya, Map<String, Object> features) {
}
