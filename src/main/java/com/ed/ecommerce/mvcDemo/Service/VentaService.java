package com.ed.ecommerce.mvcDemo.Service;

import com.ed.ecommerce.mvcDemo.Model.DetalleVenta;
import com.ed.ecommerce.mvcDemo.Model.Producto;
import com.ed.ecommerce.mvcDemo.Model.Venta;
import com.ed.ecommerce.mvcDemo.Repository.IDetalleVenta;
import com.ed.ecommerce.mvcDemo.Repository.IProducto;
import com.ed.ecommerce.mvcDemo.Repository.IVenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Este servicio es el cerebro de todo el proceso de compra.
@Service
public class VentaService {

    // Necesito los repositorios de Venta, DetalleVenta y Producto para trabajar.
    @Autowired
    private IVenta ventaRepository;

    @Autowired
    private IDetalleVenta detalleVentaRepository;

    @Autowired
    private IProducto productoRepository;

    // Este es el método principal que orquesta toda la lógica de la venta.
    public Venta procesarVenta(List<Map<String, Object>> itemsCarrito, Integer idUsuario) throws Exception {

        // 1. Primero, reviso si tengo stock suficiente para todos los productos del carrito.
        for (Map<String, Object> item : itemsCarrito) {
            Integer idProducto = (Integer) item.get("productId");
            Integer cantidad = (Integer) item.get("quantity");
            Producto producto = productoRepository.obtenerPorId(idProducto);
            if (producto == null || producto.getStock() < cantidad) {
                // Si algo no cuadra, cancelo toda la operación para no vender de más.
                throw new Exception("Stock insuficiente para el producto: " + (producto != null ? producto.getNombre() : "ID " + idProducto));
            }
        }

        // 2. Calculo el total aquí en el servidor para que sea seguro y no manipulable.
        double totalVenta = itemsCarrito.stream()
                .mapToDouble(item -> ((Number) item.get("price")).doubleValue() * ((Number) item.get("quantity")).intValue())
                .sum();

        // 3. Creo el registro principal de la venta.
        Venta nuevaVenta = new Venta();
        nuevaVenta.setIdUsuario(idUsuario);
        nuevaVenta.setFechaVenta(LocalDateTime.now());
        nuevaVenta.setTotal(totalVenta);
        nuevaVenta.setEstado("Completada"); // Marco la venta como completada.

        // Guardo la venta y obtengo el objeto con su nuevo ID.
        Venta ventaGuardada = ventaRepository.guardarVenta(nuevaVenta);

        // 4. Ahora, guardo cada producto como un detalle de esa venta y actualizo el stock.
        for (Map<String, Object> item : itemsCarrito) {
            Integer idProducto = (Integer) item.get("productId");
            Integer cantidad = (Integer) item.get("quantity");
            Double precio = ((Number) item.get("price")).doubleValue();

            // Guardo la línea de detalle.
            DetalleVenta detalle = new DetalleVenta(ventaGuardada.getIdVenta(), idProducto, cantidad, precio);
            detalleVentaRepository.guardarDetalle(detalle);

            // Actualizo el stock del producto que se vendió.
            productoRepository.actualizarStock(idProducto, cantidad);
        }

        // Devuelvo la venta completada.
        return ventaGuardada;
    }
}