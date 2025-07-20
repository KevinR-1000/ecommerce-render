package com.ed.ecommerce.mvcDemo.Controllers;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // Indica que esta clase proporciona asesoramiento global a todos los controladores.
public class AtributosGlobales {

    @ModelAttribute // Ejecuta este método antes de cualquier método @RequestMapping en cualquier controlador.
    public void agregarAtributosSesion(HttpSession session, Model model) {
        // Recupera el usuario de la sesión y lo añade al modelo, haciéndolo disponible en todas las vistas.
        Usuario usuario = (Usuario) session.getAttribute("cliente");
        if (usuario != null) {
            model.addAttribute("cliente", usuario);
        }
    }
}