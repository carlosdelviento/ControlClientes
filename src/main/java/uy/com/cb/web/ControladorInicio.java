package uy.com.cb.web;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import uy.com.cb.domain.Persona;
import uy.com.cb.servicio.PersonaService;

@Controller
@Slf4j
public class ControladorInicio {

	@Autowired
	private PersonaService personaService;
	

	@GetMapping("/")
	public String inicio(@ModelAttribute Persona persona, Model model, @AuthenticationPrincipal User user) {
		var personas = personaService.listarPersonas();
		log.info("Ejecutando el controlador Spring MVC");
		log.info("usuario que hizo login:" + user);
		model.addAttribute("listPersonas", personas);
		var saldoTotal = 0;
		for(var p: personas) {
			saldoTotal += p.getSaldo();
		}
		model.addAttribute("saldoTotal", saldoTotal);
		model.addAttribute("totalClientes", personas.size());
		return findPaginated(1, "nombre", "asc", model);
	}
	
	@GetMapping("/agregar")
	public String agregar(Persona persona) {
		return "modificar";
	}
	
	@PostMapping("/guardar")
	public String guardar(@Valid @ModelAttribute Persona persona, BindingResult result, RedirectAttributes redirectAttrs) {
		if (result.hasErrors()) {
			redirectAttrs
				.addFlashAttribute("mensaje", "Error al guardar datos")
				.addFlashAttribute("clase", "warning");
			return "modificar";
		}		
		personaService.guardar(persona);
		redirectAttrs
			.addFlashAttribute("mensaje", "Guardado con éxito")
			.addFlashAttribute("clase", "success");
		return "redirect:/";
	}
	
	@GetMapping("/editar/{idPersona}")
	public String editar(@Valid @ModelAttribute Persona persona, Model model) {
		persona = personaService.encontrarPersona(persona);
		model.addAttribute("persona", persona);
		return "modificar";
	}
	
	@GetMapping("/eliminar")
	public String eliminar(Persona persona, RedirectAttributes redirectAttrs) {
		personaService.eliminar(persona);
		redirectAttrs
			.addFlashAttribute("mensaje", "Eliminado correctamente")
			.addFlashAttribute("clase", "success");
		return "redirect:/";
	}
	
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable (value = "pageNo") int pageNo, 
			@RequestParam("sortField") String sortField,
			@RequestParam("sortDir") String sortDir,
			Model model) {
		int pageSize = 5;
		
		Page<Persona> page = personaService.findPaginated(pageNo, pageSize, sortField, sortDir);
		List<Persona> listPersonas = page.getContent();
		
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		//Contador de personas en la vista de paginacion
		var personas = personaService.listarPersonas();
		var saldoTotal = 0;
		for(var p: personas) {
			saldoTotal += p.getSaldo();
		}
		model.addAttribute("saldoTotal", saldoTotal);
		model.addAttribute("totalClientes", personas.size());
		
		//Modelo de personas con paginacion maximo 5
		model.addAttribute("listPersonas", listPersonas);
		return "index";
	}
	
	/**
	 * 
	 * Rest Services
	 */
	@ResponseBody
	@GetMapping("/webservices/personas")
	public List<Persona> getPersonas(){
		
		return personaService.listarPersonas(); 
	}
 	
	@ResponseBody
	@GetMapping("/webservices/personas/{idPersona}")
	public Persona getPersona(@PathVariable("idPersona") Persona persona) {
		
		return personaService.encontrarPersona(persona);
	}
}