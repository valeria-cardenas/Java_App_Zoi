package com.appzoi.appzoi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.appzoi.appzoi.util.Validaciones;
import org.junit.jupiter.api.Test;

class ValidacionesTests {

    @Test
    void validaDatosCorrectos() {
        assertTrue(Validaciones.texto("Valeria Gomez"));
        assertTrue(Validaciones.email("valeria@zoi.com"));
        assertTrue(Validaciones.password("Clave123"));
        assertTrue(Validaciones.telefono("300 123 4567"));
        assertTrue(Validaciones.telefonoObligatorio("300 123 4567"));
        assertTrue(Validaciones.fechaNoFutura("2025-01-01"));
        assertTrue(Validaciones.enteroEnRango(12, 0, 80));
    }

    @Test
    void rechazaDatosIncorrectos() {
        assertFalse(Validaciones.texto("A1"));
        assertFalse(Validaciones.email("correo-invalido"));
        assertFalse(Validaciones.password("123"));
        assertFalse(Validaciones.password("solominusculas"));
        assertFalse(Validaciones.password("SINNUMEROS"));
        assertFalse(Validaciones.telefono("abc"));
        assertFalse(Validaciones.telefonoObligatorio(""));
        assertFalse(Validaciones.fechaNoFutura("2999-01-01"));
        assertFalse(Validaciones.enteroEnRango(99, 0, 80));
        assertFalse(Validaciones.documento("ABC"));
    }
}
