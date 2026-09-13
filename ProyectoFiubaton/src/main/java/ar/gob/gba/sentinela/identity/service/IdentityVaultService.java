package ar.gob.gba.sentinela.identity.service;

import ar.gob.gba.sentinela.identity.client.VaultHttpClient;
import ar.gob.gba.sentinela.identity.dto.ReidentificacionRequest;
import org.springframework.stereotype.Service;

@Service
public class IdentityVaultService {

	private final VaultHttpClient vaultHttpClient;

	public IdentityVaultService(VaultHttpClient vaultHttpClient) {
		this.vaultHttpClient = vaultHttpClient;
	}

	public String reidentificar(ReidentificacionRequest request) {
		return vaultHttpClient.reidentificar(request);
	}
}
