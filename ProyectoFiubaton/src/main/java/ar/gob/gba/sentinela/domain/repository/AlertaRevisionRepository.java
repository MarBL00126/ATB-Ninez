package ar.gob.gba.sentinela.domain.repository;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRevisionRepository extends JpaRepository<AlertaRevision, Long> {

	List<AlertaRevision> findByEstadoOrderByFechaCreacionDesc(EstadoAlerta estado);
}
