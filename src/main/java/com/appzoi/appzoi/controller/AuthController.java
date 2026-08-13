package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.UsuarioEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        Object error = session.getAttribute("errorLogin");
        Object correo = session.getAttribute("loginEmail");
        if (error != null) model.addAttribute("errorLogin", error);
        if (correo != null) model.addAttribute("loginEmail", correo);
        session.removeAttribute("errorLogin");
        session.removeAttribute("loginEmail");
        return "inicio_sesion";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new UsuarioEntity());
        }
        return "registro";
    }
}
