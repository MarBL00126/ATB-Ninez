package ar.gob.gba.sentinela.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "linea102_serv_loc_zonales")
@Getter
@Setter
@NoArgsConstructor
public class Linea102Registro {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	@Column(name = "cantidad_llamados_previos_linea102")
	private Short cantidadLlamadosPreviosLinea102;

	@Column(name = "linea_origen")
	private String lineaOrigen;

	@Column(name = "tipo_llamado")
	private String tipoLlamado;

	private String motivo;

	@Column(name = "urgencia_percibida")
	private String urgenciaPercibida;

	@Column(name = "vinculo_agresor")
	private String vinculoAgresor;

	@Column(name = "derivacion_realizada")
	private String derivacionRealizada;

	@Column(name = "origen_llamado_predominante")
	private String origenLlamadoPredominante;

	@Column(name = "legajo_previo_en_runna")
	private Boolean legajoPrevioEnRunna;

	@Column(name = "meses_desde_ultima_intervencion")
	private Short mesesDesdeUltimaIntervencion;
}
