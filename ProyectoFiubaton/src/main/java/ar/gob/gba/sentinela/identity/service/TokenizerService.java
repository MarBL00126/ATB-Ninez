package ar.gob.gba.sentinela.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenizerService {

	private final String salt;

	public TokenizerService(@Value("${sentinela.identity.salt}") String salt) {
		this.salt = salt;
	}

	public String tokenizarDni(String dni) {
		if (dni == null || dni.isBlank()) {
			throw new IllegalArgumentException("El DNI es requerido");
		}
		String normalized = dni.replaceAll("\\D", "");
		return "NNYA-" + sha256(normalized + ":" + salt).substring(0, 16).toUpperCase();
	}

	private String sha256(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
		}
		catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 no disponible", ex);
		}
	}
}
