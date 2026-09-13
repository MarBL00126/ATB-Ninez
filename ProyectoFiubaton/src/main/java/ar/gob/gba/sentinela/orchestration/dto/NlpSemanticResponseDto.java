package ar.gob.gba.sentinela.orchestration.dto;

import java.util.List;

public record NlpSemanticResponseDto(double similitudLongitudinal, List<String> categoriasDetectadas) {
}
