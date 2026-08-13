package com.appzoi.appzoi.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Usuario {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String confirmPassword;

    private Integer roleId = 1;
    // En Usuario.java
    private String contrasena;

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

}