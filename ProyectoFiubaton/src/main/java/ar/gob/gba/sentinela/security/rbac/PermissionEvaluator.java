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
		return details instanceof UserAccessContext context
				&& normalize(context.localidad()).equalsIgnoreCase(normalize(localidad));
	}

	public boolean hasGlobalAccess(Authentication authentication) {
		return hasRole(authentication, Role.ADMIN) || hasRole(authentication, Role.AUDITOR);
	}

	public String scopedLocalidad(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		Object details = authentication.getDetails();
		return details instanceof UserAccessContext context ? context.localidad() : null;
	}

	public boolean hasRole(Authentication authentication, Role role) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		String expected = "ROLE_" + role.name();
		return authorities.stream().anyMatch(authority -> expected.equals(authority.getAuthority()));
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim();
	}
}
