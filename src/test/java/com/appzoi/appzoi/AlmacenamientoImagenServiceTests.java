package com.appzoi.appzoi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.appzoi.appzoi.service.AlmacenamientoImagenService;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class AlmacenamientoImagenServiceTests {

    @TempDir
    Path temporal;

    @Test
    void guardaUnaImagenPermitida() {
        AlmacenamientoImagenService servicio = new AlmacenamientoImagenService(temporal.toString());
        MockMultipartFile imagen = new MockMultipartFile(
                "foto", "mascota.png", "image/png",
                new byte[]{(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10}
        );

        String ruta = servicio.guardar(imagen, "mascotas");

        assertNotNull(ruta);
        assertTrue(ruta.startsWith("/uploads/mascotas/"));
        assertTrue(Files.exists(temporal.resolve(ruta.substring("/uploads/".length()))));
    }

    @Test
    void rechazaArchivosQueNoSonImagenes() {
        AlmacenamientoImagenService servicio = new AlmacenamientoImagenService(temporal.toString());
        MockMultipartFile archivo = new MockMultipartFile(
                "foto", "datos.txt", "text/plain", "contenido".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> servicio.guardar(archivo, "mascotas"));
    }

    @Test
    void eliminaLaImagenAlmacenada() {
        AlmacenamientoImagenService servicio = new AlmacenamientoImagenService(temporal.toString());
        MockMultipartFile imagen = new MockMultipartFile(
                "foto", "perfil.jpg", "image/jpeg",
                new byte[]{(byte) 0xff, (byte) 0xd8, (byte) 0xff, 0}
        );
        String ruta = servicio.guardar(imagen, "veterinarios");
        Path archivo = temporal.resolve(ruta.substring("/uploads/".length()));

        servicio.eliminar(ruta);

        assertTrue(Files.notExists(archivo));
    }

    @Test
    void rechazaContenidoFalsoAunqueDeclareSerImagen() {
        AlmacenamientoImagenService servicio = new AlmacenamientoImagenService(temporal.toString());
        MockMultipartFile archivo = new MockMultipartFile(
                "foto", "falso.png", "image/png", "esto no es una imagen".getBytes()
        );
        assertThrows(IllegalArgumentException.class, () -> servicio.guardar(archivo, "chat"));
    }
}
