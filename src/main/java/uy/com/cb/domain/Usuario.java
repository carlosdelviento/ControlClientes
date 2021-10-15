package uy.com.cb.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUsuario;

	@NotEmpty
	private String username;

	@NotEmpty
	private String password;

	@OneToMany
	@JoinColumn(name = "id_usuario")
	private List<Rol> roles;

	//Getters and Setters Lombok
}
