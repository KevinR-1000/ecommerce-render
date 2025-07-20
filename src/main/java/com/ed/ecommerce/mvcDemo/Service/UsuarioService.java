package com.ed.ecommerce.mvcDemo.Service;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import com.ed.ecommerce.mvcDemo.Repository.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements IUsuario {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(DataSource dataSource, BCryptPasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario validarCliente(String correo, String contrasena) {
        Usuario usuario = null;
        // CORREGIDO: Se añaden comillas dobles a "Usuario".
        String query = "SELECT \"idUsuario\", \"primerNombre\", \"segundoNombre\", \"primerApellido\", \"segundoApellido\", correo, contrasena, direccion, telefono FROM \"Usuario\" WHERE correo = ?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String contrasenaHasheada = rs.getString("contrasena");
                    if (passwordEncoder.matches(contrasena, contrasenaHasheada)) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("idUsuario"));
                        usuario.setPrimerNombre(rs.getString("primerNombre"));
                        usuario.setSegundoNombre(rs.getString("segundoNombre"));
                        usuario.setPrimerApellido(rs.getString("primerApellido"));
                        usuario.setSegundoApellido(rs.getString("segundoApellido"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setContrasena(contrasenaHasheada);
                        usuario.setDireccion(rs.getString("direccion"));
                        usuario.setTelefono(rs.getString("telefono"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    @Override
    public boolean registrar(Usuario usuario) {
        // CORREGIDO: Se añaden comillas dobles a "Usuario" y sus columnas.
        String query = "INSERT INTO \"Usuario\" (\"primerNombre\", \"segundoNombre\", \"primerApellido\", \"segundoApellido\", correo, contrasena, direccion, telefono) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection();
             PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            String contrasenaHasheada = passwordEncoder.encode(usuario.getContrasena());

            stmt.setString(1, usuario.getPrimerNombre());
            stmt.setString(2, usuario.getSegundoNombre());
            stmt.setString(3, usuario.getPrimerApellido());
            stmt.setString(4, usuario.getSegundoApellido());
            stmt.setString(5, usuario.getCorreo());
            stmt.setString(6, contrasenaHasheada);
            stmt.setString(7, usuario.getDireccion());
            stmt.setString(8, usuario.getTelefono());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setIdUsuario(rs.getInt(1));
                    }
                }
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        // CORREGIDO: Se añaden comillas dobles a "Usuario".
        String query = "SELECT \"idUsuario\", \"primerNombre\", \"segundoNombre\", \"primerApellido\", \"segundoApellido\", correo, contrasena, direccion, telefono FROM \"Usuario\"";
        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setPrimerNombre(rs.getString("primerNombre"));
                usuario.setSegundoNombre(rs.getString("segundoNombre"));
                usuario.setPrimerApellido(rs.getString("primerApellido"));
                usuario.setSegundoApellido(rs.getString("segundoApellido"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setDireccion(rs.getString("direccion"));
                usuario.setTelefono(rs.getString("telefono"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
}