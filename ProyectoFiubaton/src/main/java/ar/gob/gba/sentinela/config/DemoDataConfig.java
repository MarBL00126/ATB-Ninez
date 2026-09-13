package ar.gob.gba.sentinela.config;

import java.time.Instant;
import java.time.LocalDate;

import ar.gob.gba.sentinela.domain.entity.AlertaRevision;
import ar.gob.gba.sentinela.domain.entity.AlertaRevision.EstadoAlerta;
import ar.gob.gba.sentinela.domain.entity.Intervencion;
import ar.gob.gba.sentinela.domain.entity.NNyALegajo;
import ar.gob.gba.sentinela.domain.repository.AlertaRevisionRepository;
import ar.gob.gba.sentinela.domain.repository.IntervencionRepository;
import ar.gob.gba.sentinela.domain.repository.NNyALegajoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataConfig {

	@Bean
	CommandLineRunner seedDemoData(NNyALegajoRepository legajos, AlertaRevisionRepository alertas,
			IntervencionRepository intervenciones) {
		return args -> {
			if (legajos.count() > 0) {
				return;
			}
			crearLegajo(legajos, "NNYA-0001", (short) 8, "F", "Quilmes", LocalDate.now().minusMonths(10));
			crearLegajo(legajos, "NNYA-0002", (short) 14, "M", "Moreno", LocalDate.now().minusMonths(5));
			crearLegajo(legajos, "NNYA-0003", (short) 5, "X", "San Martin", LocalDate.now().minusMonths(2));

			crearAlerta(alertas, "NNYA-0001", 0.86, EstadoAlerta.PENDIENTE,
					"Ausentismo alto, intervencion reciente y reporte escolar concurrente.");
			crearAlerta(alertas, "NNYA-0002", 0.74, EstadoAlerta.EN_REVISION,
					"Llamados 102 recurrentes y deterioro longitudinal posible.");

			Intervencion intervencion = new Intervencion();
			intervencion.setIdNnya("NNYA-0001");
			intervencion.setFecha(Instant.now().minusSeconds(86_400));
			intervencion.setProfesional("equipo-demo");
			intervencion.setTipo("VISITA");
			intervencion.setObservacion("Contacto inicial con institucion educativa y solicitud de seguimiento.");
			intervenciones.save(intervencion);
		};
	}

	private void crearLegajo(NNyALegajoRepository repository, String id, Short edad, String sexo, String localidad,
			LocalDate fechaAlta) {
		NNyALegajo legajo = new NNyALegajo();
		legajo.setIdNnya(id);
		legajo.setEdad(edad);
		legajo.setSexo(sexo);
		legajo.setLocalidadPartido(localidad);
		legajo.setFechaAltaLegajo(fechaAlta);
		repository.save(legajo);
	}

	private void crearAlerta(AlertaRevisionRepository repository, String idNnya, Double score, EstadoAlerta estado,
			String explicacion) {
		AlertaRevision alerta = new AlertaRevision();
		alerta.setIdNnya(idNnya);
		alerta.setScoreRiesgo(score);
		alerta.setEstado(estado);
		alerta.setExplicacion(explicacion);
		repository.save(alerta);
	}
}
