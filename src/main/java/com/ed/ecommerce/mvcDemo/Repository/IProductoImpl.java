package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource; // <--- AÑADIDO
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IProductoImpl implements IProducto {

    // El DataSource gestionará las conexiones usando la configuración de application.properties
    private final DataSource dataSource;

    // A través de este constructor, Spring nos "inyecta" el DataSource automáticamente.
    @Autowired
    public IProductoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Producto> findByCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE categoria = ?";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setImagenUrl(rs.getString("imagenUrl"));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM Producto";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setIdProducto(rs.getInt("idProducto"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
                producto.setCategoria(rs.getString("categoria"));
                producto.setImagenUrl(rs.getString("imagenUrl"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    @Override
    public Producto obtenerPorId(int id) {
        Producto producto = null;
        String query = "SELECT * FROM Producto WHERE idProducto = ?";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    producto = new Producto();
                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setImagenUrl(rs.getString("imagenUrl"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return producto;
    }

    @Override
    public void guardar(Producto producto) {
        String query = "INSERT INTO Producto (nombre, descripcion, precio, stock, categoria, imagenUrl) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getDescripcion());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.getStock());
            stmt.setString(5, producto.getCategoria());
            stmt.setString(6, producto.getImagenUrl());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {
        String query = "DELETE FROM Producto WHERE idProducto = ?";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM Producto WHERE nombre LIKE ?";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + termino + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(rs.getInt("idProducto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setCategoria(rs.getString("categoria"));
                    producto.setImagenUrl(rs.getString("imagenUrl"));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    @Override
    public void actualizarStock(int idProducto, int cantidadComprada) {
        String sql = "UPDATE Producto SET stock = stock - ? WHERE idProducto = ? AND stock >= ?";
        try (Connection con = dataSource.getConnection(); // <--- CAMBIADO
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidadComprada);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadComprada);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                System.err.println("Advertencia: No se pudo actualizar el stock para el producto " + idProducto + ". Stock insuficiente.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}