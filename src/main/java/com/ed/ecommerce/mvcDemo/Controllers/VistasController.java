package com.ed.ecommerce.mvcDemo.Controllers;

import com.ed.ecommerce.mvcDemo.Model.Producto;
import com.ed.ecommerce.mvcDemo.Repository.IProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class VistasController {

    @Autowired
    private IProducto productoRepository;

    @GetMapping({"/", "/index"})
    public String mostrarIndex() {
        return "index";
    }

    @GetMapping("/accesorios")
    public String mostrarAccesorios(Model model) {
        List<Producto> accesorios = productoRepository.findByCategoria("Accesorios");
        model.addAttribute("productos", accesorios);
        return "accesorios";
    }

    @GetMapping("/celulares")
    public String mostrarCelulares(Model model) {
        List<Producto> celulares = productoRepository.findByCategoria("Celulares");
        model.addAttribute("productos", celulares);
        return "celulares";
    }

    @GetMapping("/computadoras")
    public String mostrarComputadoras(Model model) {
        List<Producto> computadoras = productoRepository.findByCategoria("Computadoras");
        model.addAttribute("productos", computadoras);
        return "computadoras";
    }

    @GetMapping("/buscar")
    public String resultadosBusqueda(@RequestParam("q") String query, Model model) {
        List<Producto> productosEncontrados = productoRepository.buscarPorNombre(query);
        model.addAttribute("productos", productosEncontrados);
        model.addAttribute("terminoBuscado", query);
        return "resultados-busqueda"; // Devuelve la nueva página de resultados
    }
    @GetMapping("/login")
    public String mostrarLogin() {
        return "ingreso";
    }
}