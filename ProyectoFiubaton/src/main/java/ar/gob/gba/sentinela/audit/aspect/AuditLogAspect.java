package ar.gob.gba.sentinela.audit.aspect;

import ar.gob.gba.sentinela.audit.entity.AuditLogEntry;
import ar.gob.gba.sentinela.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditLogAspect {

	private final AuditLogRepository repository;

	public AuditLogAspect(AuditLogRepository repository) {
		this.repository = repository;
	}

	@AfterReturning("@annotation(auditedAction)")
	public void logAction(JoinPoint joinPoint, AuditedAction auditedAction) {
		AuditLogEntry entry = new AuditLogEntry();
		entry.setAccion(auditedAction.value());
		entry.setUsuario(currentUser());
		entry.setTokenNnya(firstStringArgument(joinPoint.getArgs()));
		entry.setIp(currentIp());
		repository.save(entry);
	}

	private String currentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null ? "anonymous" : authentication.getName();
	}

	private String currentIp() {
		if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
			HttpServletRequest request = attributes.getRequest();
			return request.getRemoteAddr();
		}
		return "n/a";
	}

	private String firstStringArgument(Object[] args) {
		for (Object arg : args) {
			if (arg instanceof String value && value.startsWith("NNYA-")) {
				return value;
			}
		}
		return null;
	}
}
