package com.ed.ecommerce.mvcDemo.Service;

import com.ed.ecommerce.mvcDemo.Model.DetalleVenta;
import com.ed.ecommerce.mvcDemo.Model.Producto;
import com.ed.ecommerce.mvcDemo.Model.Venta;
import com.ed.ecommerce.mvcDemo.Repository.IDetalleVenta;
import com.ed.ecommerce.mvcDemo.Repository.IProducto;
import com.ed.ecommerce.mvcDemo.Repository.IVenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class VentaService {

    @Autowired
    private IVenta ventaRepository;

    @Autowired
    private IDetalleVenta detalleVentaRepository;

    @Autowired
    private IProducto productoRepository;

    // Procesa la venta completa como una transacción atómica.
    @Transactional(rollbackFor = Exception.class)
    public Venta procesarVenta(List<Map<String, Object>> itemsCarrito, Integer idUsuario) throws Exception {

        for (Map<String, Object> item : itemsCarrito) {
            Integer idProducto = (Integer) item.get("productId");
            Integer cantidad = (Integer) item.get("quantity");
            Producto producto = productoRepository.obtenerPorId(idProducto);
            if (producto == null || producto.getStock() < cantidad) {
                throw new Exception("Stock insuficiente para el producto: " + (producto != null ? producto.getNombre() : "ID " + idProducto));
            }
        }

        double totalVenta = itemsCarrito.stream()
                .mapToDouble(item -> ((Number) item.get("price")).doubleValue() * ((Number) item.get("quantity")).intValue())
                .sum();

        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdUsuario(idUsuario);
        nuevaVenta.setFechaVenta(LocalDateTime.now());
        nuevaVenta.setTotal(totalVenta);
        nuevaVenta.setEstado("Completada");

        Venta ventaGuardada = ventaRepository.guardarVenta(nuevaVenta);

        for (Map<String, Object> item : itemsCarrito) {
            Integer idProducto = (Integer) item.get("productId");
            Integer cantidad = (Integer) item.get("quantity");
            Double precio = ((Number) item.get("price")).doubleValue();

            DetalleVenta detalle = new DetalleVenta(ventaGuardada.getIdVenta(), idProducto, cantidad, precio);
            detalleVentaRepository.guardarDetalle(detalle);

            productoRepository.actualizarStock(idProducto, cantidad);
        }

        return ventaGuardada;
    }
}