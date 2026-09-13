package ar.gob.gba.sentinela.domain.repository;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.Intervencion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntervencionRepository extends JpaRepository<Intervencion, Long> {

	List<Intervencion> findByIdNnyaOrderByFechaDesc(String idNnya);
}
