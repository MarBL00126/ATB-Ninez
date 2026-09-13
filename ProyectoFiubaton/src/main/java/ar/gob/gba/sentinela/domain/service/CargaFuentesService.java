package ar.gob.gba.sentinela.domain.service;

import ar.gob.gba.sentinela.audit.aspect.AuditedAction;
import ar.gob.gba.sentinela.domain.entity.ClubesColoniasReg;
import ar.gob.gba.sentinela.domain.entity.DesarrolloSocialReg;
import ar.gob.gba.sentinela.domain.entity.EducacionRegistro;
import ar.gob.gba.sentinela.domain.entity.JusticiaRegistro;
import ar.gob.gba.sentinela.domain.entity.Linea102Registro;
import ar.gob.gba.sentinela.domain.entity.SaludRegistro;
import ar.gob.gba.sentinela.domain.repository.ClubesColoniasRegRepository;
import ar.gob.gba.sentinela.domain.repository.DesarrolloSocialRegRepository;
import ar.gob.gba.sentinela.domain.repository.EducacionRegistroRepository;
import ar.gob.gba.sentinela.domain.repository.JusticiaRegistroRepository;
import ar.gob.gba.sentinela.domain.repository.Linea102RegistroRepository;
import ar.gob.gba.sentinela.domain.repository.SaludRegistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CargaFuentesService {

	private final EducacionRegistroRepository educacionRepository;
	private final SaludRegistroRepository saludRepository;
	private final Linea102RegistroRepository linea102Repository;
	private final DesarrolloSocialRegRepository desarrolloSocialRepository;
	private final JusticiaRegistroRepository justiciaRepository;
	private final ClubesColoniasRegRepository clubesRepository;

	public CargaFuentesService(EducacionRegistroRepository educacionRepository,
			SaludRegistroRepository saludRepository,
			Linea102RegistroRepository linea102Repository,
			DesarrolloSocialRegRepository desarrolloSocialRepository,
			JusticiaRegistroRepository justiciaRepository,
			ClubesColoniasRegRepository clubesRepository) {
		this.educacionRepository = educacionRepository;
		this.saludRepository = saludRepository;
		this.linea102Repository = linea102Repository;
		this.desarrolloSocialRepository = desarrolloSocialRepository;
		this.justiciaRepository = justiciaRepository;
		this.clubesRepository = clubesRepository;
	}

	@AuditedAction("INGESTA_EDUCACION")
	@Transactional
	public EducacionRegistro guardarEducacion(EducacionRegistro registro) {
		return educacionRepository.save(registro);
	}

	@Transactional
	public SaludRegistro guardarSalud(SaludRegistro registro) {
		return saludRepository.save(registro);
	}

	@Transactional
	public Linea102Registro guardarLinea102(Linea102Registro registro) {
		return linea102Repository.save(registro);
	}

	@Transactional
	public DesarrolloSocialReg guardarDesarrolloSocial(DesarrolloSocialReg registro) {
		return desarrolloSocialRepository.save(registro);
	}

	@Transactional
	public JusticiaRegistro guardarJusticia(JusticiaRegistro registro) {
		return justiciaRepository.save(registro);
	}

	@Transactional
	public ClubesColoniasReg guardarClubes(ClubesColoniasReg registro) {
		return clubesRepository.save(registro);
	}
}
