package ar.gob.gba.sentinela.security.jwt;

import java.util.Arrays;
import java.util.List;

import ar.gob.gba.sentinela.security.rbac.UserAccessContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

	public Authentication authenticate(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return null;
		}
		String token = authorizationHeader.substring("Bearer ".length()).trim();
		String[] parts = token.split(":", 3);
		if (parts.length < 2 || parts[0].isBlank()) {
			return null;
		}
		List<SimpleGrantedAuthority> authorities = Arrays.stream(parts[1].split(","))
				.map(String::trim)
				.filter(role -> !role.isBlank())
				.map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
				.map(SimpleGrantedAuthority::new)
				.toList();
		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(parts[0], token, authorities);
		if (parts.length == 3 && !parts[2].isBlank()) {
			authentication.setDetails(new UserAccessContext(parts[2].trim()));
		}
		return authentication;
	}
}
