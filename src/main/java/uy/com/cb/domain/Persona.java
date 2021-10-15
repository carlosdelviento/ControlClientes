package uy.com.cb.domain;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "persona")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Persona implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPersona;

	@NotEmpty(message = "{NotEmpty.persona.nombre}")
	@Size(min=3, max=15)
	private String nombre;

	@NotEmpty(message = "{NotEmpty.persona.apellido}")
	@Pattern(regexp = "^[A-Za-z]{3,15}", message = "{Pattern.persona.apellido}")
	private String apellido;

	@NotEmpty
	@Email
	private String email;
	
	@Pattern(regexp = "^[0-9]{6,10}", message = "{Pattern.persona.telefono}")
	private String telefono;
	
	@NotEmpty(message = "{NotEmpty.persona.saldo}")
	@DecimalMin(value = "10.0", message = "El saldo mínimo tiene que ser 10")
	@DecimalMax(value = "10000.0", message = "El saldo máximo tiene que ser 10000")
	private Double saldo;
	
	//Getters and Setters Lombok
}
