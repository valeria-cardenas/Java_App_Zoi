package com.appzoi.appzoi.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlmacenamientoImagenService {

    private static final long TAMANO_MAXIMO = 5 * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/png", "image/jpeg", "image/webp", "image/gif"
    );
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );

    private final Path directorioRaiz;

    public AlmacenamientoImagenService(@Value("${app.upload.dir:uploads}") String directorio) {
        this.directorioRaiz = Path.of(directorio).toAbsolutePath().normalize();
    }

    public String guardar(MultipartFile archivo, String carpeta) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("La imagen no puede superar 5 MB.");
        }

        String tipo = archivo.getContentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo.toLowerCase())) {
            throw new IllegalArgumentException("Formato no permitido. Usa PNG, JPG, WebP o GIF.");
        }
        validarFirma(archivo, tipo.toLowerCase());

        Path destinoCarpeta = directorioRaiz.resolve(carpeta).normalize();
        if (!destinoCarpeta.startsWith(directorioRaiz)) {
            throw new IllegalArgumentException("Destino de imagen no valido.");
        }

        String nombre = UUID.randomUUID() + EXTENSIONES.get(tipo.toLowerCase());
        Path destino = destinoCarpeta.resolve(nombre);
        try {
            Files.createDirectories(destinoCarpeta);
            try (InputStream entrada = archivo.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No fue posible guardar la imagen.", exception);
        }
        return "/uploads/" + carpeta + "/" + nombre;
    }

    private void validarFirma(MultipartFile archivo, String tipo) {
        try (InputStream entrada = archivo.getInputStream()) {
            byte[] cabecera = entrada.readNBytes(12);
            boolean valida = switch (tipo) {
                case "image/png" -> cabecera.length >= 8 && (cabecera[0] & 0xff) == 0x89
                        && cabecera[1] == 'P' && cabecera[2] == 'N' && cabecera[3] == 'G';
                case "image/jpeg" -> cabecera.length >= 3 && (cabecera[0] & 0xff) == 0xff
                        && (cabecera[1] & 0xff) == 0xd8 && (cabecera[2] & 0xff) == 0xff;
                case "image/gif" -> cabecera.length >= 6 && cabecera[0] == 'G' && cabecera[1] == 'I'
                        && cabecera[2] == 'F' && cabecera[3] == '8';
                case "image/webp" -> cabecera.length >= 12 && cabecera[0] == 'R' && cabecera[1] == 'I'
                        && cabecera[2] == 'F' && cabecera[3] == 'F' && cabecera[8] == 'W'
                        && cabecera[9] == 'E' && cabecera[10] == 'B' && cabecera[11] == 'P';
                default -> false;
            };
            if (!valida) throw new IllegalArgumentException("El contenido del archivo no corresponde a una imagen válida.");
        } catch (IOException exception) {
            throw new IllegalStateException("No fue posible validar la imagen.", exception);
        }
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
            // La limpieza del archivo no debe impedir la operacion principal.
        }
    }
}
