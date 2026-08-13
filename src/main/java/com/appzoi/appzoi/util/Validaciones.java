package com.appzoi.appzoi.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public final class Validaciones {

    private static final Pattern TEXTO = Pattern.compile("^[\\p{L} ]{2,80}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern EMAIL_REGISTRO = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@(gmail\\.com|outlook\\.com)$", Pattern.CASE_INSENSITIVE
    );
    private static final Pattern TELEFONO = Pattern.compile("^$|^[0-9 +()-]{7,20}$");
    private static final Pattern PASSWORD = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$"
    );
    private static final Pattern TARJETA = Pattern.compile("^$|^[A-Za-z0-9 -]{3,80}$");

    private Validaciones() {
    }

    public static boolean texto(String valor) {
        return valor != null && TEXTO.matcher(valor.trim()).matches();
    }

    public static boolean email(String valor) {
        return valor != null && valor.trim().length() <= 120
                && EMAIL.matcher(valor.trim()).matches();
    }

    public static boolean emailRegistroPermitido(String valor) {
        return email(valor) && EMAIL_REGISTRO.matcher(valor.trim()).matches();
    }

    public static boolean password(String valor) {
        return valor != null && PASSWORD.matcher(valor).matches();
    }

    public static boolean telefono(String valor) {
        return valor == null || TELEFONO.matcher(valor.trim()).matches();
    }

    public static boolean telefonoObligatorio(String valor) {
        return valor != null && !valor.isBlank() && TELEFONO.matcher(valor.trim()).matches();
    }

    public static boolean tarjetaProfesional(String valor) {
        return valor == null || TARJETA.matcher(valor.trim()).matches();
    }

    public static boolean textoOpcional(String valor) {
        return valor == null || valor.isBlank() || texto(valor);
    }

    public static boolean longitudMaxima(String valor, int maxima) {
        return valor == null || valor.length() <= maxima;
    }

    public static boolean enteroEnRango(Integer valor, int minimo, int maximo) {
        return valor == null || (valor >= minimo && valor <= maximo);
    }

    public static boolean fechaNoFutura(String valor) {
        if (valor == null || valor.isBlank()) return true;
        try {
            return !LocalDate.parse(valor).isAfter(LocalDate.now());
        } catch (RuntimeException exception) {
            return false;
        }
    }

    public static boolean documento(String valor) {
        return valor == null || valor.isBlank() || valor.trim().matches("[0-9]{5,30}");
    }
}
