package uy.com.cb.dao;

import uy.com.cb.domain.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

//auto generar métodos comunes
public interface UsuarioDao extends JpaRepository<Usuario, Long> {
	Usuario findByUsername(String username);
}
