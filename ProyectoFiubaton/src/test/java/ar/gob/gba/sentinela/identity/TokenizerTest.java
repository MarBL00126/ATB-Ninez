package ar.gob.gba.sentinela.identity;

import static org.assertj.core.api.Assertions.assertThat;

import ar.gob.gba.sentinela.identity.service.TokenizerService;
import org.junit.jupiter.api.Test;

class TokenizerTest {

	@Test
	void tokenIsStableAndDoesNotExposeDni() {
		TokenizerService tokenizer = new TokenizerService("test-salt");

		String token = tokenizer.tokenizarDni("12.345.678");

		assertThat(token).startsWith("NNYA-");
		assertThat(token).doesNotContain("12345678");
		assertThat(token).isEqualTo(tokenizer.tokenizarDni("12345678"));
	}
}
