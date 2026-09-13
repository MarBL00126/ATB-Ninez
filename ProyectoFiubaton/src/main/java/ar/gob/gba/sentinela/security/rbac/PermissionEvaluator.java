package ar.gob.gba.sentinela.security.rbac;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class PermissionEvaluator {

	public boolean canAccessLocalidad(Authentication authentication, String localidad) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		if (hasRole(authentication, Role.ADMIN) || hasRole(authentication, Role.AUDITOR)) {
			return true;
		}
		Object details = authentication.getDetails();
		return details instanceof UserAccessContext context && context.localidad().equalsIgnoreCase(localidad);
	}

	public boolean hasRole(Authentication authentication, Role role) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		String expected = "ROLE_" + role.name();
		return authorities.stream().anyMatch(authority -> expected.equals(authority.getAuthority()));
	}
}
