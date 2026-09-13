package ar.gob.gba.sentinela.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "maestro_nnya")
@Getter
@Setter
@NoArgsConstructor
public class NNyALegajo {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	private Short edad;
	private String sexo;

	@Column(name = "localidad_partido")
	private String localidadPartido;

	@Column(name = "fecha_alta_legajo")
	private LocalDate fechaAltaLegajo;
}
