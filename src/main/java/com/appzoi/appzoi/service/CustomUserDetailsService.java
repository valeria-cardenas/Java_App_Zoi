package com.appzoi.appzoi.service;

import com.appzoi.appzoi.config.AdminUserSeeder;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(obtenerRol(usuario))
                .build();
    }

    private String obtenerRol(UsuarioEntity usuario) {
        if ("ADMIN".equals(usuario.getTipoPerfil())
                || AdminUserSeeder.ADMIN_EMAIL.equalsIgnoreCase(usuario.getEmail())) {
            return "ADMIN";
        }
        if ("VETERINARIO".equals(usuario.getTipoPerfil())) {
            return "VETERINARIO";
        }
        if ("DUENO".equals(usuario.getTipoPerfil())) {
            return "DUENO";
        }
        return "USER";
    }
}
