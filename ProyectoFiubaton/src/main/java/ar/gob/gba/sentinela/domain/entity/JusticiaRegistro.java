package ar.gob.gba.sentinela.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "justicia_seguridad")
@Getter
@Setter
@NoArgsConstructor
public class JusticiaRegistro {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	@Column(name = "denuncia_violencia_domestica_en_el_hogar")
	private Boolean denunciaViolenciaDomesticaEnElHogar;

	@Column(name = "antecedentes_penales_convivientes_violencia_abuso")
	private Boolean antecedentesPenalesConvivientesViolenciaAbuso;
}
