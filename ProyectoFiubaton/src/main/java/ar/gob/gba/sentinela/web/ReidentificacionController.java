package ar.gob.gba.sentinela.web;

import ar.gob.gba.sentinela.identity.dto.ReidentificacionRequest;
import ar.gob.gba.sentinela.identity.service.IdentityVaultService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identidad")
public class ReidentificacionController {

	private final IdentityVaultService service;

	public ReidentificacionController(IdentityVaultService service) {
		this.service = service;
	}

	@PostMapping("/desenmascarar")
	@PreAuthorize("hasRole('ADMIN') or hasRole('AUDITOR')")
	public String reidentificar(@Valid @RequestBody ReidentificacionRequest request) {
		return service.reidentificar(request);
	}
}
