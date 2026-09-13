package ar.gob.gba.sentinela.vector.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class LocalEmbeddingService {

	public static final int DIMENSIONS = 384;

	public double[] embed(String text) {
		double[] vector = new double[DIMENSIONS];
		String normalized = normalize(text);
		String[] tokens = normalized.split("\\s+");
		for (String token : tokens) {
			if (token.length() < 3) {
				continue;
			}
			int index = Math.floorMod(token.hashCode(), DIMENSIONS);
			vector[index] += 1.0;
			for (String ngram : ngrams(token)) {
				vector[Math.floorMod(ngram.hashCode(), DIMENSIONS)] += 0.35;
			}
		}
		normalizeVector(vector);
		return vector;
	}

	public String toPgVector(double[] vector) {
		StringBuilder builder = new StringBuilder("[");
		for (int i = 0; i < vector.length; i++) {
			if (i > 0) {
				builder.append(',');
			}
			builder.append(String.format(Locale.US, "%.6f", vector[i]));
		}
		return builder.append(']').toString();
	}

	public double cosine(double[] left, double[] right) {
		double value = 0.0;
		for (int i = 0; i < Math.min(left.length, right.length); i++) {
			value += left[i] * right[i];
		}
		return value;
	}

	public double[] parseVector(String value) {
		if (value == null || value.isBlank()) {
			return new double[DIMENSIONS];
		}
		String cleaned = value.replace("[", "").replace("]", "");
		String[] parts = cleaned.split(",");
		double[] vector = new double[DIMENSIONS];
		for (int i = 0; i < Math.min(parts.length, DIMENSIONS); i++) {
			vector[i] = Double.parseDouble(parts[i].trim());
		}
		return vector;
	}

	private String normalize(String text) {
		String value = text == null ? "" : text.toLowerCase(Locale.ROOT);
		value = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
		return value.replaceAll("[^a-z0-9ñ\\s]", " ").replaceAll("\\s+", " ").trim();
	}

	private String[] ngrams(String token) {
		if (token.length() <= 4) {
			return new String[] { token };
		}
		String[] grams = new String[token.length() - 2];
		for (int i = 0; i < token.length() - 2; i++) {
			grams[i] = token.substring(i, i + 3);
		}
		return grams;
	}

	private void normalizeVector(double[] vector) {
		double norm = 0.0;
		for (double value : vector) {
			norm += value * value;
		}
		norm = Math.sqrt(norm);
		if (norm == 0.0) {
			vector[Math.floorMod(hash("empty"), DIMENSIONS)] = 1.0;
			return;
		}
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i] / norm;
		}
	}

	private int hash(String value) {
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
			return digest[0];
		}
		catch (NoSuchAlgorithmException ex) {
			return value.hashCode();
		}
	}
}
