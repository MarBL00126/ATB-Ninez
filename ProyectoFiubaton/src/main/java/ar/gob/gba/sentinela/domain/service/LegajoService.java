package ar.gob.gba.sentinela.domain.service;

import java.util.List;

import ar.gob.gba.sentinela.audit.aspect.AuditedAction;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.repository.NNyALegajoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LegajoService {

	private final NNyALegajoRepository repository;
	private final AlertaRevisionRepository alertaRepository;

	public LegajoService(NNyALegajoRepository repository, AlertaRevisionRepository alertaRepository) {
		this.repository = repository;
		this.alertaRepository = alertaRepository;
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
		NNyALegajo saved = repository.save(legajo);
		asegurarAlertaPendienteBasica(saved);
		return saved;
	}

	@Transactional
	public void asegurarBandejaPendiente(List<NNyALegajo> legajos) {
		legajos.forEach(this::asegurarAlertaPendienteBasica);
	}

	private void asegurarAlertaPendienteBasica(NNyALegajo legajo) {
		asegurarAlertaPendiente(legajo.getIdNnya(), 0.72,
				"Seguimiento local persistido en BDD para " + legajo.getLocalidadPartido()
						+ "; categorias=[seguimiento_local, evaluacion_pendiente]");
	}

	private void asegurarAlertaPendiente(String idNnya, Double score, String explicacion) {
		AlertaRevision alerta = alertaRepository
				.findFirstByIdNnyaAndEstadoInOrderByFechaCreacionDesc(idNnya,
						List.of(AlertaRevision.EstadoAlerta.PENDIENTE, AlertaRevision.EstadoAlerta.EN_REVISION))
				.orElseGet(AlertaRevision::new);
		alerta.setIdNnya(idNnya);
		alerta.setScoreRiesgo(score);
		alerta.setExplicacion(explicacion);
		alerta.setEstado(AlertaRevision.EstadoAlerta.PENDIENTE);
		alertaRepository.save(alerta);
	}
}
