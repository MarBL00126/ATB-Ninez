package ar.gob.gba.sentinela.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record ReidentificacionRequest(
		@NotBlank String tokenNnya,
		@NotBlank String motivoLegal,
		@NotBlank String firmaProfesional) {
}
