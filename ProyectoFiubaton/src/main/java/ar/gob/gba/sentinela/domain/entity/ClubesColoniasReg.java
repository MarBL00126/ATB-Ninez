package ar.gob.gba.sentinela.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clubes_colonias")
@Getter
@Setter
@NoArgsConstructor
public class ClubesColoniasReg {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	@Column(name = "inscripto_club_o_colonia")
	private Boolean inscriptoClubOColonia;

	@Column(name = "ausencias_reiteradas_sin_aviso")
	private Boolean ausenciasReiteradasSinAviso;

	@Column(name = "observaciones_informales_profesor_monitor")
	private Boolean observacionesInformalesProfesorMonitor;
}
