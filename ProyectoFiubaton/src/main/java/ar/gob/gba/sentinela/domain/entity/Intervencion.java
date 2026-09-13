package ar.gob.gba.sentinela.domain.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "intervenciones")
@Getter
@Setter
@NoArgsConstructor
public class Intervencion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "id_nnya", nullable = false, length = 32)
	private String idNnya;

	@Column(nullable = false)
	private Instant fecha = Instant.now();

	@Column(nullable = false)
	private String profesional;

	@Column(nullable = false)
	private String tipo;

	@Column(length = 4000)
	private String observacion;
}
