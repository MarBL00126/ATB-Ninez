package ar.gob.gba.sentinela.orchestration.dto;

import java.util.Map;

public record MlPredictResponseDto(double scoreRiesgo, Map<String, Double> shapValues) {
}
