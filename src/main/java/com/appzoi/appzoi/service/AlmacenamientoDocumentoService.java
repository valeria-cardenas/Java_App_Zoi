package com.appzoi.appzoi.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlmacenamientoDocumentoService {

    private static final long TAMANO_MAXIMO = 10 * 1024 * 1024;
    private final Path directorioRaiz;

    public AlmacenamientoDocumentoService(@Value("${app.upload.dir:uploads}") String directorio) {
        this.directorioRaiz = Path.of(directorio).toAbsolutePath().normalize();
    }

    public String guardarPdf(MultipartFile archivo, String carpeta) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El PDF no puede superar 10 MB.");
        }
        if (!"application/pdf".equalsIgnoreCase(archivo.getContentType()) || !tieneFirmaPdf(archivo)) {
            throw new IllegalArgumentException("El titulo o certificado debe ser un archivo PDF valido.");
        }

        Path destinoCarpeta = directorioRaiz.resolve(carpeta).normalize();
        if (!destinoCarpeta.startsWith(directorioRaiz)) {
            throw new IllegalArgumentException("Destino de documento no valido.");
        }
        Path destino = destinoCarpeta.resolve(UUID.randomUUID() + ".pdf");
        try {
            Files.createDirectories(destinoCarpeta);
            try (InputStream entrada = archivo.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No fue posible guardar el documento.", exception);
        }
        return "/uploads/" + carpeta + "/" + destino.getFileName();
    }

    public void eliminar(String rutaPublica) {
        if (rutaPublica == null || !rutaPublica.startsWith("/uploads/")) {
            return;
        }
        Path archivo = directorioRaiz.resolve(rutaPublica.substring("/uploads/".length())).normalize();
        if (!archivo.startsWith(directorioRaiz)) {
            return;
        }
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException ignored) {
            // La limpieza no debe impedir que el perfil se actualice.
        }
    }

    private boolean tieneFirmaPdf(MultipartFile archivo) {
        try (InputStream entrada = archivo.getInputStream()) {
            byte[] firma = entrada.readNBytes(5);
            return firma.length == 5 && firma[0] == '%' && firma[1] == 'P'
                    && firma[2] == 'D' && firma[3] == 'F' && firma[4] == '-';
        } catch (IOException exception) {
            return false;
        }
    }
}
