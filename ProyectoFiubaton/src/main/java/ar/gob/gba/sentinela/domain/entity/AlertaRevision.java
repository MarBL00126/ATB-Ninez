package ar.gob.gba.sentinela.domain.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alertas_revision")
@Getter
@Setter
@NoArgsConstructor
public class AlertaRevision {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "id_nnya", nullable = false, length = 32)
	private String idNnya;

	@Column(nullable = false)
	private Instant fechaCreacion = Instant.now();

	@Column(nullable = false)
	private Double scoreRiesgo;

	@Column(length = 4000)
	private String explicacion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EstadoAlerta estado = EstadoAlerta.PENDIENTE;

	public enum EstadoAlerta {
		PENDIENTE,
		EN_REVISION,
		CONFIRMADA,
		DESCARTADA
	}
}
