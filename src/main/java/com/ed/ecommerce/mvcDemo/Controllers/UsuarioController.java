package com.ed.ecommerce.mvcDemo.Controllers;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/Eccomerce")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyecta el servicio de usuario vía constructor.
    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Muestra la página de inicio de sesión.
    @GetMapping("/login")
    public String mostrarIngreso() {
        return "ingreso";
    }

    // Muestra la página principal de la aplicación.
    @GetMapping({"/", "/index"})
    public String mostrarIndex() {
        return "index";
    }

    // Valida las credenciales del usuario e inicia sesión.
    @PostMapping("/validar")
    public String validarCliente(@RequestParam("correo") String correo,
                                 @RequestParam("contrasena") String contrasena,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.validarCliente(correo, contrasena);

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            redirectAttributes.addFlashAttribute("success", "¡Bienvenido de vuelta, " + usuario.getPrimerNombre() + "!");
            return "redirect:/Eccomerce/index";
        } else {
            redirectAttributes.addFlashAttribute("error", "Correo o contraseña inválidos.");
            return "redirect:/Eccomerce/login";
        }
    }

    // Cierra la sesión actual del usuario.
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("info", "Has cerrado sesión correctamente.");
        return "redirect:/Eccomerce/login";
    }

    // Guarda un nuevo usuario en la base de datos.
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        boolean registrado = usuarioService.registrar(usuario);
        if (registrado) {
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/Eccomerce/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está en uso. Intenta con otro.");
            return "redirect:/Eccomerce/login";
        }
    }
}