package com.ed.ecommerce.mvcDemo.Controllers;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Este controlador se encarga de todo lo relacionado con los usuarios: login, registro, logout, etc.
@Controller
@RequestMapping("/Eccomerce")
public class UsuarioController {

    // Necesito el servicio de usuario para la lógica de negocio.
    private final UsuarioService usuarioService;

    // Inyecto la dependencia del servicio a través del constructor.
    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Este método solo me muestra la página de login.
    @GetMapping("/login")
    public String mostrarIngreso() {
        return "ingreso";
    }

    // Hago que tanto la raíz como "/index" me lleven a la página principal.
    @GetMapping({"/", "/index"})
    public String mostrarIndex() {
        return "index";
    }

    // Aquí valido las credenciales del usuario cuando intenta iniciar sesión.
    @PostMapping("/validar")
    public String validarCliente(@RequestParam("correo") String correo,
                                 @RequestParam("contrasena") String contrasena,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.validarCliente(correo, contrasena);

        // Si el usuario existe, lo guardo en la sesión y le doy la bienvenida.
        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            // Uso FlashAttribute para que el mensaje sobreviva después de redirigir.
            redirectAttributes.addFlashAttribute("success", "¡Bienvenido de vuelta, " + usuario.getPrimerNombre() + "!");
            return "redirect:/Eccomerce/index";
        } else {
            // Si las credenciales son incorrectas, muestro un error y lo mando de nuevo al login.
            redirectAttributes.addFlashAttribute("error", "Correo o contraseña inválidos.");
            return "redirect:/Eccomerce/login";
        }
    }

    // Este método me sirve para cerrar la sesión del usuario.
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalido la sesión para borrar los datos del usuario.
        session.invalidate();
        // Le aviso al usuario que cerró sesión correctamente.
        redirectAttributes.addFlashAttribute("info", "Has cerrado sesión correctamente.");
        return "redirect:/Eccomerce/login";
    }

    // Con este método registro a un nuevo usuario en la base de datos.
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        boolean registrado = usuarioService.registrar(usuario);
        // Si el registro fue exitoso, lo mando al login para que inicie sesión.
        if (registrado) {
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ya puedes iniciar sesión.");
            return "redirect:/Eccomerce/login";
        } else {
            // Si el correo ya existe, le notifico el error.
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está en uso. Intenta con otro.");
            return "redirect:/Eccomerce/login";
        }
    }
}