package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IVentaImpl implements IVenta {

    private final DataSource dataSource;

    // Constructor para la inyección del origen de datos.
    @Autowired
    public IVentaImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Guarda una venta y devuelve el objeto con su ID generado.
    @Override
    public Venta guardarVenta(Venta venta) {
        String sql = "INSERT INTO \"Venta\" (\"idUsuario\", \"fechaVenta\", total, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, venta.getIdUsuario());
            ps.setTimestamp(2, Timestamp.valueOf(venta.getFechaVenta()));
            ps.setDouble(3, venta.getTotal());
            ps.setString(4, venta.getEstado());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                venta.setIdVenta(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venta;
    }

    // Obtiene una lista con todas las ventas de la base de datos.
    @Override
    public List<Venta> obtenerVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM \"Venta\"";
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Venta venta = new Venta(
                        rs.getInt("idVenta"),
                        rs.getInt("idUsuario"),
                        rs.getTimestamp("fechaVenta").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getString("estado")
                );
                ventas.add(venta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

    // Obtiene una venta específica por su identificador.
    @Override
    public Venta obtenerVentaPorId(int idVenta) {
        Venta venta = null;
        String sql = "SELECT * FROM \"Venta\" WHERE \"idVenta\" = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                venta = new Venta(
                        rs.getInt("idVenta"),
                        rs.getInt("idUsuario"),
                        rs.getTimestamp("fechaVenta").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getString("estado")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return venta;
    }

    // Actualiza el estado de una venta existente.
    @Override
    public void actualizarEstadoVenta(int idVenta, String estado) {
        String sql = "UPDATE \"Venta\" SET estado = ? WHERE \"idVenta\" = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idVenta);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}