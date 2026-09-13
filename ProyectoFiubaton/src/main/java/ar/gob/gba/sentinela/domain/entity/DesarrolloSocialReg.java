package ar.gob.gba.sentinela.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "desarrollosocial")
@Getter
@Setter
@NoArgsConstructor
public class DesarrolloSocialReg {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	@Column(name = "auh_u_otra_prestacion_interrumpida")
	private Boolean auhUOtraPrestacionInterrumpida;

	@Column(name = "situacion_habitacional_reportada")
	private String situacionHabitacionalReportada;

	@Column(name = "adultos_convivientes_a_cargo")
	private Short adultosConvivientesACargo;
}
