package ar.gob.gba.sentinela.domain.repository;

import java.util.List;

import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NNyALegajoRepository extends JpaRepository<NNyALegajo, String> {

	List<NNyALegajo> findByLocalidadPartidoIgnoreCase(String localidadPartido);
}
