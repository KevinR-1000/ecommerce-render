package com.ed.ecommerce.mvcDemo.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration // Indica que esta clase define beans de configuración de Spring.
public class SecurityConfig {

    @Bean // Convierte objeto BCryptPasswordEncoder como un bean en el contexto de la aplicación.
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Retorna una nueva instancia de BCryptPasswordEncoder, usada para encriptar contraseñas de forma segura.
    }
}