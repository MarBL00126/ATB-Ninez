package ar.gob.gba.sentinela.vector.dto;

import jakarta.validation.constraints.NotBlank;

public record PaperSearchRequest(@NotBlank String query, Integer limit) {
}
