package com.appzoi.appzoi;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.appzoi.appzoi.service.AlmacenamientoDocumentoService;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class AlmacenamientoDocumentoServiceTests {

    @TempDir
    Path temporal;

    @Test
    void guardaUnPdfValido() {
        AlmacenamientoDocumentoService servicio = new AlmacenamientoDocumentoService(temporal.toString());
        MockMultipartFile pdf = new MockMultipartFile(
                "tituloPdf", "titulo.pdf", "application/pdf", "%PDF-1.7 contenido".getBytes()
        );
        String ruta = servicio.guardarPdf(pdf, "titulos-veterinarios");
        assertTrue(ruta.endsWith(".pdf"));
        assertTrue(Files.exists(temporal.resolve(ruta.substring("/uploads/".length()))));
    }

    @Test
    void rechazaContenidoPdfInvalido() {
        AlmacenamientoDocumentoService servicio = new AlmacenamientoDocumentoService(temporal.toString());
        MockMultipartFile archivo = new MockMultipartFile(
                "tituloPdf", "falso.pdf", "application/pdf", "no es pdf".getBytes()
        );
        assertThrows(IllegalArgumentException.class,
                () -> servicio.guardarPdf(archivo, "titulos-veterinarios"));
    }
}
