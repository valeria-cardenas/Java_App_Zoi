package com.appzoi.appzoi.config;

import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.AdministradorRepositorio;
import com.appzoi.appzoi.model.AdministradorEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AdminUserSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserSeeder.class);

    public static final String ADMIN_EMAIL = "admin@zoi.com";

    @Bean
    public CommandLineRunner crearUsuarioAdmin(
            UsuarioRepositorio usuarioRepository,
            AdministradorRepositorio administradorRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.initial-password:}") String initialPassword
    ) {
        return args -> {
            UsuarioEntity admin = usuarioRepository.findByEmail(ADMIN_EMAIL).orElse(null);
            if (admin == null) {
                if (initialPassword == null || initialPassword.isBlank()) {
                    LOGGER.warn("No existe el administrador principal. Define ZOI_ADMIN_PASSWORD antes de iniciar Zoi.");
                } else {
                    admin = new UsuarioEntity();
                    admin.setNombre("Administrador");
                    admin.setApellido("Zoi");
                    admin.setEmail(ADMIN_EMAIL);
                    admin.setPassword(passwordEncoder.encode(initialPassword));
                    admin.setTipoPerfil("ADMIN");
                    usuarioRepository.save(admin);
                }
            }

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
