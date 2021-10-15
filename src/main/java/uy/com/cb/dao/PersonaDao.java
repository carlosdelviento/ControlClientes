package uy.com.cb.dao;

import uy.com.cb.domain.Persona;

import org.springframework.data.jpa.repository.JpaRepository;

//auto generar métodos comunes
public interface PersonaDao extends JpaRepository<Persona, Long> {
}
