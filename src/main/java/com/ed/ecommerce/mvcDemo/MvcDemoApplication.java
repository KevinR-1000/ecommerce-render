package com.ed.ecommerce.mvcDemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
// CORRECCIÓN 1: Importar la anotación para activar las transacciones.
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
// CORRECCIÓN 2: Añadir esta anotación. Es el "interruptor general" que le dice a Spring:
// "¡Oye, busca y gestiona todas las anotaciones @Transactional que encuentres!"
@EnableTransactionManagement
public class MvcDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner connectionTestRunner(DataSource dataSource) {
		return args -> {
			System.out.println("\n--- Iniciando prueba de conexión a la BD al arranque ---");
			try (Connection con = dataSource.getConnection();
				 Statement stmt = con.createStatement();
				 // Usamos NOW() que es estándar en PostgreSQL.
				 ResultSet rs = stmt.executeQuery("SELECT NOW() AS CurrentDateTime")) {

				if (rs.next()) {
					System.out.println("¡Conexión a la base de datos exitosa!");
					System.out.println("Fecha/Hora actual de la BD: " + rs.getString("CurrentDateTime"));
				} else {
					System.out.println("Conexión exitosa, pero la consulta de prueba no devolvió resultados.");
				}
			} catch (SQLException e) {
				System.err.println("¡FALLO la prueba de conexión a la BD al arranque!");
				System.err.println("Mensaje de error: " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("¡ERROR INESPERADO durante la prueba de conexión a la BD!");
				System.err.println("Mensaje de error: " + e.getMessage());
				e.printStackTrace();
			}
		};
	}
}