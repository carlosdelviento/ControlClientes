package uy.com.cb.servicio;

import java.util.List;

import uy.com.cb.dao.PersonaDao;
import uy.com.cb.domain.Persona;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonaServiceImpl implements PersonaService{
	
	@Autowired
	private PersonaDao personaDao;

	@Override
	@Transactional(readOnly = true)
	@Cacheable("personas")
	public List<Persona> listarPersonas() {
		
		try {
			 Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return (List<Persona>) personaDao.findAll();
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(value="persona", key="#p0"),
		@CacheEvict(value="personas", allEntries=true)})
	public void guardar(Persona persona) {
		try {
			 Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		personaDao.save(persona);
	}

	@Override
	@Transactional
	@Caching(evict = {
			@CacheEvict(value="persona", key="#p0"),
			@CacheEvict(value="personas", allEntries=true)})
	public void eliminar(Persona persona) {
		try {
			 Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		personaDao.delete(persona);
	}

	@Override
	@Transactional(readOnly = true)
	@Caching(evict = {
			@CacheEvict(value="persona", key="#p0"),
			@CacheEvict(value="personas", allEntries=true)})
	public Persona encontrarPersona(Persona persona) {

		try {
			 Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return personaDao.findById(persona.getIdPersona()).orElse(null);
	}

	@Override
	public Page<Persona> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.personaDao.findAll(pageable);
	}
}
