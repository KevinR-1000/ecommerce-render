package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Venta;
import java.util.List;

public interface IVenta {
    Venta guardarVenta(Venta venta);
    List<Venta> obtenerVentas();
    Venta obtenerVentaPorId(int idVenta);
    void actualizarEstadoVenta(int idVenta, String estado);
}