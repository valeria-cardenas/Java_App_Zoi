package com.appzoi.appzoi.config;

import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.AdministradorRepositorio;
import com.appzoi.appzoi.model.AdministradorEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserSeeder {

    public static final String ADMIN_EMAIL = "admin@zoi.com";
    public static final String ADMIN_PASSWORD = "Admin12345";

    @Bean
    public CommandLineRunner crearUsuarioAdmin(
            UsuarioRepositorio usuarioRepository,
            AdministradorRepositorio administradorRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            UsuarioEntity admin = usuarioRepository.findByEmail(ADMIN_EMAIL)
                    .orElseGet(UsuarioEntity::new);

            admin.setNombre("Administrador");
            admin.setApellido("Zoi");
            admin.setEmail(ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setTipoPerfil("ADMIN");

            usuarioRepository.save(admin);

            // Garantiza el registro en la tabla Administrador para el principal
            // y migra administradores creados antes de existir esta tabla.
            for (UsuarioEntity usuario : usuarioRepository.findAllByTipoPerfil("ADMIN")) {
                if (!administradorRepository.existsByUsuario(usuario)) {
                    AdministradorEntity administrador = new AdministradorEntity();
                    administrador.setUsuario(usuario);
                    administradorRepository.save(administrador);
                }
            }
        };
    }
}
