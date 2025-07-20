
package com.ed.ecommerce.mvcDemo.Repository;

import com.ed.ecommerce.mvcDemo.Model.Usuario;
import java.util.List;

//Operaciones de datos de usuario
public interface IUsuario {

    Usuario validarCliente(String correo, String contrasena);

    boolean registrar(Usuario usuario);

    // Obtiene una lista de todos los usuarios registrados.
    List<Usuario> obtenerUsuarios();

}