package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.DetalleVenta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class IDetalleVentaImpl implements IDetalleVenta {

    private final DataSource dataSource;

    // Constructor para la inyección del origen de datos.
    @Autowired
    public IDetalleVentaImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Guarda el detalle de un producto dentro de una venta.
    @Override
    public void guardarDetalle(DetalleVenta detalleVenta) {
        String sql = "INSERT INTO \"DetalleVenta\" (\"idVenta\", \"idProducto\", cantidad, \"precioUnitario\") VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalleVenta.getIdVenta());
            ps.setInt(2, detalleVenta.getIdProducto());
            ps.setInt(3, detalleVenta.getCantidad());
            ps.setDouble(4, detalleVenta.getPrecioUnitario());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}