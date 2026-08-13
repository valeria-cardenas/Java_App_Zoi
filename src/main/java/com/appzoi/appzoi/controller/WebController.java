package com.appzoi.appzoi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String inicio() {
        return "inicio"; // Renderiza inicio.html
    }

    @GetMapping("/proposito")
    public String proposito() {
        return "proposito";
    }

    @GetMapping("/beneficios")
    public String beneficios() {
        return "beneficios";
    }

    @GetMapping("/valores")
    public String valores() {
        return "valores";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso_denegado";
    }
}
