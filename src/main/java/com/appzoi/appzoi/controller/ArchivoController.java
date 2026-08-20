package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.ConversacionEntity;
import com.appzoi.appzoi.model.MascotaEntity;
import com.appzoi.appzoi.model.MensajeEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.ConversacionRepositorio;
import com.appzoi.appzoi.repository.MascotaRepositorio;
import com.appzoi.appzoi.repository.MensajeRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ArchivoController {
    private final Path raiz;
    private final UsuarioRepositorio usuarios;
    private final MascotaRepositorio mascotas;
    private final VeterinarioPerfilRepositorio veterinarios;
    private final MensajeRepositorio mensajes;
    private final ConversacionRepositorio conversaciones;

    public ArchivoController(@Value("${app.upload.dir:uploads}") String directorio,
            UsuarioRepositorio usuarios, MascotaRepositorio mascotas,
            VeterinarioPerfilRepositorio veterinarios, MensajeRepositorio mensajes,
            ConversacionRepositorio conversaciones) {
        this.raiz = Path.of(directorio).toAbsolutePath().normalize();
        this.usuarios = usuarios;
        this.mascotas = mascotas;
        this.veterinarios = veterinarios;
        this.mensajes = mensajes;
        this.conversaciones = conversaciones;
    }

    @GetMapping("/uploads/{carpeta}/{nombre:.+}")
    public ResponseEntity<Resource> descargar(@PathVariable String carpeta, @PathVariable String nombre,
            Principal principal) {
        UsuarioEntity usuario = usuarios.findByEmail(principal.getName()).orElseThrow();
        String rutaPublica = "/uploads/" + carpeta + "/" + nombre;
        if (!autorizado(rutaPublica, usuario)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);
        }

        Path archivo = raiz.resolve(carpeta).resolve(nombre).normalize();
        if (!archivo.startsWith(raiz) || !Files.isRegularFile(archivo)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        }
        try {
            Resource recurso = new UrlResource(archivo.toUri());
            String tipo = Files.probeContentType(archivo);
            MediaType mediaType = tipo == null
                    ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(tipo);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + nombre.replace("\"", "") + "\"")
                    .body(recurso);
        } catch (Exception exception) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        }
    }

    private boolean autorizado(String ruta, UsuarioEntity usuario) {
        if ("ADMIN".equals(usuario.getTipoPerfil())) return true;
        if (ruta.startsWith("/uploads/veterinarios/")
                || ruta.startsWith("/uploads/titulos-veterinarios/")) {
            return veterinarios.findByFotoUrl(ruta).isPresent()
                    || veterinarios.findByTituloUrl(ruta).isPresent();
        }
        if (ruta.startsWith("/uploads/chat/")) {
            return mensajes.findByImagenUrl(ruta)
                    .map(mensaje -> participa(mensaje, usuario)).orElse(false);
        }
        MascotaEntity mascota = ruta.startsWith("/uploads/mascotas/")
                ? mascotas.findByFotoUrl(ruta).orElse(null)
                : mascotas.findByCarnetVacunacionUrl(ruta).orElse(null);
        return mascota != null && (mascota.getDueno().getId().equals(usuario.getId())
                || conversaciones.existsByMascotaAndVeterinario_Usuario(mascota, usuario));
    }

    private boolean participa(MensajeEntity mensaje, UsuarioEntity usuario) {
        ConversacionEntity chat = mensaje.getConversacion();
        return chat.getDueno().getId().equals(usuario.getId())
                || chat.getVeterinario().getUsuario().getId().equals(usuario.getId());
    }
}
