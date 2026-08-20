package com.appzoi.appzoi;

import static org.mockito.Mockito.*;

import com.appzoi.appzoi.config.AdminUserSeeder;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.AdministradorRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

class AdminUserSeederTests {
    @Test
    void noRestableceLaContrasenaDeUnAdministradorExistente() throws Exception {
        UsuarioRepositorio usuarios = mock(UsuarioRepositorio.class);
        AdministradorRepositorio administradores = mock(AdministradorRepositorio.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        UsuarioEntity admin = new UsuarioEntity();
        admin.setEmail(AdminUserSeeder.ADMIN_EMAIL);
        admin.setPassword("hash-personalizado");
        admin.setTipoPerfil("ADMIN");
        when(usuarios.findByEmail(AdminUserSeeder.ADMIN_EMAIL)).thenReturn(Optional.of(admin));
        when(usuarios.findAllByTipoPerfil("ADMIN")).thenReturn(List.of(admin));
        when(administradores.existsByUsuario(admin)).thenReturn(true);

        CommandLineRunner runner = new AdminUserSeeder()
                .crearUsuarioAdmin(usuarios, administradores, encoder, "otra-clave");
        runner.run();

        verify(encoder, never()).encode(anyString());
        verify(usuarios, never()).save(admin);
        org.junit.jupiter.api.Assertions.assertEquals("hash-personalizado", admin.getPassword());
    }
}
