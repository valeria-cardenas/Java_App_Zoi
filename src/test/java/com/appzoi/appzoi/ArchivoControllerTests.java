package com.appzoi.appzoi;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.appzoi.appzoi.controller.ArchivoController;
import com.appzoi.appzoi.model.MascotaEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.server.ResponseStatusException;

class ArchivoControllerTests {
    @TempDir
    Path temporal;

    @Test
    void permiteAlDuenoConsultarLaFotoDeSuMascota() throws Exception {
        Dependencias d = dependencias();
        UsuarioEntity dueno = usuario(1, "dueno@zoi.com", "DUENO");
        MascotaEntity mascota = new MascotaEntity();
        mascota.setDueno(dueno);
        mascota.setFotoUrl("/uploads/mascotas/foto.png");
        Files.createDirectories(temporal.resolve("mascotas"));
        Files.write(temporal.resolve("mascotas/foto.png"), new byte[]{1, 2, 3});
        when(d.usuarios.findByEmail(dueno.getEmail())).thenReturn(Optional.of(dueno));
        when(d.mascotas.findByFotoUrl(mascota.getFotoUrl())).thenReturn(Optional.of(mascota));

        var respuesta = d.controller.descargar("mascotas", "foto.png", principal(dueno));

        assertEquals(200, respuesta.getStatusCode().value());
    }

    @Test
    void rechazaAUnUsuarioAjenoALaMascota() {
        Dependencias d = dependencias();
        UsuarioEntity dueno = usuario(1, "dueno@zoi.com", "DUENO");
        UsuarioEntity ajeno = usuario(2, "ajeno@zoi.com", "VETERINARIO");
        MascotaEntity mascota = new MascotaEntity();
        mascota.setDueno(dueno);
        mascota.setFotoUrl("/uploads/mascotas/privada.png");
        when(d.usuarios.findByEmail(ajeno.getEmail())).thenReturn(Optional.of(ajeno));
        when(d.mascotas.findByFotoUrl(mascota.getFotoUrl())).thenReturn(Optional.of(mascota));
        when(d.conversaciones.existsByMascotaAndVeterinario_Usuario(mascota, ajeno)).thenReturn(false);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> d.controller.descargar("mascotas", "privada.png", principal(ajeno)));
        assertEquals(403, error.getStatusCode().value());
    }

    private Dependencias dependencias() {
        UsuarioRepositorio usuarios = mock(UsuarioRepositorio.class);
        MascotaRepositorio mascotas = mock(MascotaRepositorio.class);
        VeterinarioPerfilRepositorio veterinarios = mock(VeterinarioPerfilRepositorio.class);
        MensajeRepositorio mensajes = mock(MensajeRepositorio.class);
        ConversacionRepositorio conversaciones = mock(ConversacionRepositorio.class);
        return new Dependencias(usuarios, mascotas, conversaciones,
                new ArchivoController(temporal.toString(), usuarios, mascotas, veterinarios, mensajes, conversaciones));
    }

    private UsuarioEntity usuario(int id, String correo, String rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setEmail(correo);
        usuario.setTipoPerfil(rol);
        return usuario;
    }

    private Principal principal(UsuarioEntity usuario) {
        return usuario::getEmail;
    }

    private record Dependencias(UsuarioRepositorio usuarios, MascotaRepositorio mascotas,
                                ConversacionRepositorio conversaciones, ArchivoController controller) {
    }
}
