package ar.gob.gba.sentinela.domain.repository;

import ar.gob.gba.sentinela.domain.entity.SaludRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaludRegistroRepository extends JpaRepository<SaludRegistro, String> {
}
