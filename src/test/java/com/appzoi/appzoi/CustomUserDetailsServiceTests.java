package com.appzoi.appzoi;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.appzoi.appzoi.config.AdminUserSeeder;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.service.CustomUserDetailsService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

class CustomUserDetailsServiceTests {

    @Mock
    private UsuarioRepositorio usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @BeforeEach
    void preparar() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void asignaRolAdministradorPorCorreoProtegido() {
        UsuarioEntity usuario = usuario(AdminUserSeeder.ADMIN_EMAIL, null);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        UserDetails detalles = service.loadUserByUsername(usuario.getEmail());

        assertTrue(detalles.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void asignaRolSegunPerfil() {
        UsuarioEntity usuario = usuario("veterinario@zoi.com", "VETERINARIO");
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        UserDetails detalles = service.loadUserByUsername(usuario.getEmail());

        assertTrue(detalles.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_VETERINARIO")));
    }

    private UsuarioEntity usuario(String email, String perfil) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail(email);
        usuario.setPassword("hash");
        usuario.setTipoPerfil(perfil);
        return usuario;
    }
}
