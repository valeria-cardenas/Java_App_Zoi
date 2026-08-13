package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.config.AdminUserSeeder;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @GetMapping("/encuesta")
    public String mostrarEncuesta() {
        return "encuesta"; // Debe coincidir exactamente con el nombre del archivo .html
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Principal principal) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(principal.getName()).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equals(usuario.getTipoPerfil())
                || AdminUserSeeder.ADMIN_EMAIL.equalsIgnoreCase(usuario.getEmail())) {
            return "redirect:/admin";
        }
        if ("VETERINARIO".equals(usuario.getTipoPerfil())) {
            return "redirect:/veterinario/home";
        }
        if ("DUENO".equals(usuario.getTipoPerfil())) {
            return "redirect:/dueno/home";
        }
        return "redirect:/seleccionar-perfil";
    }
}
