package com.ed.ecommerce.mvcDemo.Service;

import com.ed.ecommerce.mvcDemo.Model.Producto;
import com.ed.ecommerce.mvcDemo.Repository.IProducto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final IProducto iProducto;

    @Autowired
    public ProductoService(IProducto iProducto) {
        this.iProducto = iProducto;
    }

    public List<Producto> obtenerTodos() {
        return iProducto.obtenerTodos();
    }

    public Producto obtenerPorId(int id) {
        return iProducto.obtenerPorId(id);
    }

    public void guardar(Producto producto) {
        iProducto.guardar(producto);
    }

    public void eliminar(int id) {
        iProducto.eliminar(id);
    }
}