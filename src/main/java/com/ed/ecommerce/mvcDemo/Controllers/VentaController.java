package com.ed.ecommerce.mvcDemo.Controllers;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Service.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping("/procesar-pago")
    public ResponseEntity<?> procesarPago(@RequestBody List<Map<String, Object>> itemsCarrito, HttpSession session) {
        // Obtenemos el usuario de la sesión
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null) {
            return new ResponseEntity<>("Usuario no autenticado.", HttpStatus.UNAUTHORIZED);
        }

        try {
            ventaService.procesarVenta(itemsCarrito, usuarioLogueado.getIdUsuario());
            return new ResponseEntity<>("Venta procesada exitosamente.", HttpStatus.OK);
        } catch (Exception e) {
            // Si el servicio lanza una excepción, la capturamos
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}