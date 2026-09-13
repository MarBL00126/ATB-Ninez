package ar.gob.gba.sentinela.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salud")
@Getter
@Setter
@NoArgsConstructor
public class SaludRegistro {

	@Id
	@Column(name = "id_nnya", length = 32)
	private String idNnya;

	@Column(name = "controles_pediatricos_atrasados")
	private Boolean controlesPediatricosAtrasados;

	@Column(name = "consultas_guardia_lesiones_pococlaras_ult12m")
	private Short consultasGuardiaLesionesPocoClarasUlt12m;

	@Column(name = "bajo_peso_o_retraso_desarrollo")
	private Boolean bajoPesoORetrasoDesarrollo;

	@Column(name = "consultas_salud_mental_nnya_o_adulto_a_cargo")
	private Boolean consultasSaludMentalNnyaOAdultoACargo;

	@Column(name = "atencion_por_autolesion_o_consumo")
	private Boolean atencionPorAutolesionOConsumo;
}
