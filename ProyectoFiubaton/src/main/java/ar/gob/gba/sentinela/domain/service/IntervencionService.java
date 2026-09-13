package ar.gob.gba.sentinela.domain.service;

import java.util.List;

import ar.gob.gba.sentinela.audit.aspect.AuditedAction;
import ar.gob.gba.sentinela.domain.entity.Intervencion;
import ar.gob.gba.sentinela.domain.repository.IntervencionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntervencionService {

	private final IntervencionRepository repository;

	public IntervencionService(IntervencionRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<Intervencion> listarPorLegajo(String idNnya) {
		return repository.findByIdNnyaOrderByFechaDesc(idNnya);
	}

	@AuditedAction("REGISTRAR_INTERVENCION")
	@Transactional
	public Intervencion registrar(String idNnya, Intervencion intervencion) {
		intervencion.setIdNnya(idNnya);
		return repository.save(intervencion);
	}
}
