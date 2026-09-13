package ar.gob.gba.sentinela.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "educacion")
@Getter
@Setter
@NoArgsConstructor
public class EducacionRegistro {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	@Column(name = "ausentismo_dias_ultimo_mes")
	private Short ausentismoDiasUltimoMes;

	@Column(name = "patron_faltas_mismos_dias")
	private Boolean patronFaltasMismosDias;

	@Column(name = "desercion_o_abandono_intermitente")
	private Boolean desercionOAbandonoIntermitente;

	@Column(name = "cambios_escuela_ultimo_anio")
	private Short cambiosEscuelaUltimoAnio;

	@Column(name = "caida_rendimiento_subita")
	private Boolean caidaRendimientoSubita;

	@Column(name = "reporte_gabinete_psicopedagogico")
	private Boolean reporteGabinetePsicopedagogico;

	@Column(name = "lesiones_reportadas_por_docentes")
	private Boolean lesionesReportadasPorDocentes;
}
