package ar.gob.gba.sentinela.domain.service;

import java.util.List;

import ar.gob.gba.sentinela.audit.aspect.AuditedAction;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.NNyALegajoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LegajoService {

	private final NNyALegajoRepository repository;

	public LegajoService(NNyALegajoRepository repository) {
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public List<NNyALegajo> listar(String localidad) {
		return localidad == null || localidad.isBlank()
				? repository.findAll()
				: repository.findByLocalidadPartidoIgnoreCase(localidad);
	}

	@AuditedAction("LECTURA_LEGAJO")
	@Transactional(readOnly = true)
	public NNyALegajo obtener(String idNnya) {
		return repository.findById(idNnya).orElseThrow();
	}

	@AuditedAction("GUARDAR_LEGAJO")
	@Transactional
	public NNyALegajo guardar(NNyALegajo legajo) {
		return repository.save(legajo);
	}
}
