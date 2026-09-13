package ar.gob.gba.sentinela.audit.repository;

import ar.gob.gba.sentinela.audit.entity.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {
}
