// RUTA: src/main/java/com/ed/ecommerce/mvcDemo/Controllers/ProductoApiController.java

package com.ed.ecommerce.mvcDemo.Controllers;

import com.ed.ecommerce.mvcDemo.Model.Producto;
import com.ed.ecommerce.mvcDemo.Repository.IProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoApiController {

    @Autowired
    private IProducto productoRepository;

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam("term") String term) {
        // Limita la cantidad de sugerencias para no sobrecargar el frontend
        List<Producto> sugerencias = productoRepository.buscarPorNombre(term).stream().limit(10).toList();
        return ResponseEntity.ok(sugerencias);
    }
}