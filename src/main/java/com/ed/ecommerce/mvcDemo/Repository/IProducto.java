package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Producto;

import java.util.List;

public interface IProducto {

    List<Producto> obtenerTodos();
    Producto obtenerPorId(int id);
    void guardar(Producto producto);
    void eliminar(int id);
    List<Producto> findByCategoria(String categoria);
    List<Producto> buscarPorNombre(String termino);
    void actualizarStock(int idProducto, int cantidadComprada);
}