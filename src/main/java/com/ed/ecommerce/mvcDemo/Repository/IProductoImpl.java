package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IProductoImpl implements IProducto {

    private final DataSource dataSource;

    // Constructor para la inyección del origen de datos.
    @Autowired
    public IProductoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Busca productos que coincidan con una categoría específica.
    @Override
    public List<Producto> findByCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM \"Producto\" WHERE categoria = ?";
        try (Connection con = dataSource.getConnection();
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

    // Obtiene una lista con todos los productos disponibles.
    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT * FROM \"Producto\"";
        try (Connection con = dataSource.getConnection();
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

    // Obtiene un producto específico a partir de su ID.
    @Override
    public Producto obtenerPorId(int id) {
        Producto producto = null;
        String query = "SELECT * FROM \"Producto\" WHERE \"idProducto\" = ?";
        try (Connection con = dataSource.getConnection();
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

    // Guarda un nuevo producto en la base de datos.
    @Override
    public void guardar(Producto producto) {
        String query = "INSERT INTO \"Producto\" (nombre, descripcion, precio, stock, categoria, \"imagenUrl\") VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
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

    // Elimina un producto de la base de datos por su ID.
    @Override
    public void eliminar(int id) {
        String query = "DELETE FROM \"Producto\" WHERE \"idProducto\" = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Busca productos cuyo nombre contenga el término de búsqueda.
    @Override
    public List<Producto> buscarPorNombre(String termino) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM \"Producto\" WHERE nombre LIKE ?";
        try (Connection con = dataSource.getConnection();
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

    // Actualiza el stock de un producto, restando la cantidad comprada.
    @Override
    public void actualizarStock(int idProducto, int cantidadComprada) {
        System.out.println("--- INTENTANDO ACTUALIZAR STOCK ---");
        System.out.println("Producto ID: " + idProducto + ", Cantidad a reducir: " + cantidadComprada);

        String sql = "UPDATE \"Producto\" SET stock = stock - ? WHERE \"idProducto\" = ? AND stock >= ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidadComprada);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadComprada);

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Filas afectadas por la actualización: " + filasAfectadas);

            if (filasAfectadas == 0) {
                System.err.println("Advertencia: No se pudo actualizar el stock para el producto " + idProducto + ". Stock insuficiente o producto no encontrado.");
            } else {
                System.out.println("¡STOCK ACTUALIZADO EXITOSAMENTE PARA EL PRODUCTO " + idProducto + "!");
            }

        } catch (SQLException e) {
            System.err.println("--- ¡ERROR SQL AL ACTUALIZAR STOCK! ---");
            e.printStackTrace();
        }
    }
}