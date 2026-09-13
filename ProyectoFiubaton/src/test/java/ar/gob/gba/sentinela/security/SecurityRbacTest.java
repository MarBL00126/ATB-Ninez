package ar.gob.gba.sentinela.security;

import static org.assertj.core.api.Assertions.assertThat;

import ar.gob.gba.sentinela.security.jwt.JwtTokenProvider;
import ar.gob.gba.sentinela.security.rbac.PermissionEvaluator;
import ar.gob.gba.sentinela.security.rbac.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class SecurityRbacTest {

	private final JwtTokenProvider tokenProvider = new JwtTokenProvider();
	private final PermissionEvaluator permissionEvaluator = new PermissionEvaluator();

	@Test
	void adminCanAccessAnyLocalidad() {
		Authentication authentication = tokenProvider.authenticate("Bearer ana:ADMIN:Quilmes");

		assertThat(permissionEvaluator.hasRole(authentication, Role.ADMIN)).isTrue();
		assertThat(permissionEvaluator.canAccessLocalidad(authentication, "Moreno")).isTrue();
	}

	@Test
	void operatorIsLimitedToOwnLocalidad() {
		Authentication authentication = tokenProvider.authenticate("Bearer leo:OPERADOR_102:Quilmes");

		assertThat(permissionEvaluator.canAccessLocalidad(authentication, "Quilmes")).isTrue();
		assertThat(permissionEvaluator.canAccessLocalidad(authentication, "Moreno")).isFalse();
	}
}
