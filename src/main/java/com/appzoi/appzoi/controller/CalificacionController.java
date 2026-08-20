package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.CalificacionVeterinarioEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.model.VeterinarioPerfilEntity;
import com.appzoi.appzoi.repository.CalificacionVeterinarioRepositorio;
import com.appzoi.appzoi.repository.ConversacionRepositorio;
import com.appzoi.appzoi.repository.MensajeRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CalificacionController {
    private final UsuarioRepositorio usuarios;
    private final VeterinarioPerfilRepositorio veterinarios;
    private final CalificacionVeterinarioRepositorio calificaciones;
    private final ConversacionRepositorio conversaciones;
    private final MensajeRepositorio mensajes;

    public CalificacionController(UsuarioRepositorio usuarios, VeterinarioPerfilRepositorio veterinarios,
            CalificacionVeterinarioRepositorio calificaciones, ConversacionRepositorio conversaciones,
            MensajeRepositorio mensajes) {
        this.usuarios = usuarios;
        this.veterinarios = veterinarios;
        this.calificaciones = calificaciones;
        this.conversaciones = conversaciones;
        this.mensajes = mensajes;
    }

    @PostMapping("/dueno/veterinarios/{id}/calificar")
    public String calificar(@PathVariable Integer id, @RequestParam Integer estrellas,
            @RequestParam String comentario, Principal principal, RedirectAttributes flash) {
        UsuarioEntity dueno = usuarios.findByEmail(principal.getName()).orElseThrow();
        VeterinarioPerfilEntity veterinario = veterinarios.findById(id).orElse(null);
        String texto = comentario == null ? "" : comentario.trim();

        if (!puedeCalificar(dueno, veterinario)) {
            flash.addFlashAttribute("errorCalificacion",
                    "Podrás calificar cuando el veterinario haya respondido tu consulta.");
            return "redirect:/dueno/veterinarios/" + id;
        }
        if (estrellas == null || estrellas < 1 || estrellas > 5
                || texto.length() < 3 || texto.length() > 800) {
            flash.addFlashAttribute("errorCalificacion",
                    "Selecciona de 1 a 5 estrellas y escribe un comentario de 3 a 800 caracteres.");
            return "redirect:/dueno/veterinarios/" + id;
        }

        CalificacionVeterinarioEntity calificacion = calificaciones
                .findByDuenoAndVeterinario(dueno, veterinario)
                .orElseGet(CalificacionVeterinarioEntity::new);
        calificacion.setDueno(dueno);
        calificacion.setVeterinario(veterinario);
        calificacion.setEstrellas(estrellas);
        calificacion.setComentario(texto);
        calificaciones.save(calificacion);
        flash.addFlashAttribute("successCalificacion", "Tu calificación fue guardada.");
        return "redirect:/dueno/veterinarios/" + id;
    }

    private boolean puedeCalificar(UsuarioEntity dueno, VeterinarioPerfilEntity veterinario) {
        return veterinario != null
                && conversaciones.existsByDuenoAndVeterinario(dueno, veterinario)
                && mensajes.existsByConversacion_DuenoAndConversacion_VeterinarioAndAutor(
                        dueno, veterinario, veterinario.getUsuario());
    }
}
