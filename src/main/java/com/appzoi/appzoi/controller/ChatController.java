package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.*;
import com.appzoi.appzoi.repository.*;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import com.appzoi.appzoi.service.AlmacenamientoImagenService;

@Controller
public class ChatController {
    private final UsuarioRepositorio usuarios;
    private final MascotaRepositorio mascotas;
    private final VeterinarioPerfilRepositorio veterinarios;
    private final ConversacionRepositorio conversaciones;
    private final MensajeRepositorio mensajes;
    private final AlmacenamientoImagenService almacenamientoImagen;
    private final CalificacionVeterinarioRepositorio calificaciones;

    public ChatController(UsuarioRepositorio usuarios, MascotaRepositorio mascotas,
            VeterinarioPerfilRepositorio veterinarios, ConversacionRepositorio conversaciones,
            MensajeRepositorio mensajes, AlmacenamientoImagenService almacenamientoImagen,
            CalificacionVeterinarioRepositorio calificaciones) {
        this.usuarios = usuarios; this.mascotas = mascotas; this.veterinarios = veterinarios;
        this.conversaciones = conversaciones; this.mensajes = mensajes;
        this.almacenamientoImagen = almacenamientoImagen;
        this.calificaciones = calificaciones;
    }

    @GetMapping("/dueno/veterinarios")
    public String veterinarios(Model model, Principal principal) {
        UsuarioEntity dueno = usuario(principal);
        java.util.List<VeterinarioPerfilEntity> lista = veterinarios.findAll();
        lista.forEach(v -> { v.setPromedioCalificacion(calificaciones.promedio(v)); v.setTotalCalificaciones(calificaciones.countByVeterinario(v)); });
        model.addAttribute("veterinarios", lista);
        model.addAttribute("mascotas", mascotas.findByDueno(dueno));
        model.addAttribute("conversaciones", conversaciones.findByDuenoOrderByActualizadaEnDesc(dueno));
        model.addAttribute("respuestasNuevas", conversaciones.countByDuenoAndPendienteVeterinarioFalse(dueno));
        return "veterinarios_lista";
    }

    @GetMapping("/dueno/veterinarios/{id}")
    public String perfil(@PathVariable Integer id, Model model, Principal principal,
            RedirectAttributes flash) {
        VeterinarioPerfilEntity perfil = veterinarios.findById(id).orElse(null);
        if (perfil == null) { flash.addFlashAttribute("errorPerfil", "Veterinario no encontrado."); return "redirect:/dueno/veterinarios"; }
        model.addAttribute("perfil", perfil);
        model.addAttribute("mascotas", mascotas.findByDueno(usuario(principal)));
        model.addAttribute("calificaciones", calificaciones.findByVeterinarioOrderByActualizadaEnDesc(perfil));
        model.addAttribute("promedio", calificaciones.promedio(perfil));
        model.addAttribute("totalCalificaciones", calificaciones.countByVeterinario(perfil));
        model.addAttribute("miCalificacion", calificaciones.findByDuenoAndVeterinario(usuario(principal), perfil).orElse(null));
        UsuarioEntity dueno = usuario(principal);
        model.addAttribute("puedeCalificar", mensajes.existsByConversacion_DuenoAndConversacion_VeterinarioAndAutor(
                dueno, perfil, perfil.getUsuario()));
        return "veterinario_publico";
    }

    @PostMapping("/dueno/veterinarios/{vetId}/chat")
    public String abrir(@PathVariable Integer vetId, @RequestParam Integer mascotaId,
            Principal principal, RedirectAttributes flash) {
        UsuarioEntity dueno = usuario(principal);
        MascotaEntity mascota = mascotas.findByIdAndDueno(mascotaId, dueno).orElse(null);
        VeterinarioPerfilEntity vet = veterinarios.findById(vetId).orElse(null);
        if (mascota == null || vet == null) { flash.addFlashAttribute("errorPerfil", "No se pudo iniciar la conversación."); return "redirect:/dueno/veterinarios"; }
        ConversacionEntity chat = conversaciones.findByDuenoAndVeterinarioAndMascota(dueno, vet, mascota).orElseGet(() -> {
            ConversacionEntity c = new ConversacionEntity(); c.setDueno(dueno); c.setVeterinario(vet); c.setMascota(mascota); return conversaciones.save(c);
        });
        return "redirect:/chat/" + chat.getId();
    }

    @GetMapping("/chat/{id}")
    public String chat(@PathVariable Integer id, Principal principal, Model model,
            RedirectAttributes flash) {
        UsuarioEntity actual = usuario(principal);
        ConversacionEntity chat = conversaciones.findById(id).orElse(null);
        if (chat == null || !participa(chat, actual)) { flash.addFlashAttribute("errorPerfil", "No tienes acceso a esa conversación."); return inicio(actual); }
        mensajes.marcarLeidos(chat, actual);
        model.addAttribute("chat", chat);
        model.addAttribute("mensajes", mensajes.findByConversacionOrderByEnviadoEnAsc(chat));
        model.addAttribute("esDueno", chat.getDueno().getId().equals(actual.getId()));
        return "chat";
    }

    @PostMapping("/chat/{id}/mensajes")
    public String enviar(@PathVariable Integer id, @RequestParam(required = false) String contenido,
            @RequestParam(required = false) MultipartFile imagen,
            Principal principal, RedirectAttributes flash) {
        UsuarioEntity actual = usuario(principal);
        ConversacionEntity chat = conversaciones.findById(id).orElse(null);
        if (chat == null || !participa(chat, actual)) { flash.addFlashAttribute("errorPerfil", "No tienes acceso a esa conversación."); return inicio(actual); }
        String texto = contenido == null ? "" : contenido.trim();
        boolean tieneImagen = imagen != null && !imagen.isEmpty();
        boolean esVeterinario = chat.getVeterinario().getUsuario().getId().equals(actual.getId());
        if (esVeterinario && texto.isEmpty()) { flash.addFlashAttribute("errorPerfil", "Como veterinario debes escribir una respuesta; no puede quedar vacía."); return "redirect:/chat/" + id; }
        if ((texto.isEmpty() && !tieneImagen) || texto.length() > 2000) { flash.addFlashAttribute("errorPerfil", "Escribe un mensaje o adjunta una imagen. El texto admite máximo 2000 caracteres."); return "redirect:/chat/" + id; }
        String imagenUrl = null;
        if (tieneImagen) {
            try { imagenUrl = almacenamientoImagen.guardar(imagen, "chat"); }
            catch (IllegalArgumentException | IllegalStateException exception) {
                flash.addFlashAttribute("errorPerfil", exception.getMessage()); return "redirect:/chat/" + id;
            }
        }
        MensajeEntity mensaje = new MensajeEntity(); mensaje.setConversacion(chat); mensaje.setAutor(actual);
        mensaje.setContenido(texto); mensaje.setImagenUrl(imagenUrl); mensajes.save(mensaje);
        chat.setPendienteVeterinario(chat.getDueno().getId().equals(actual.getId()));
        chat.setEstado(esVeterinario ? "RESPONDIDA" : "PENDIENTE");
        conversaciones.save(chat);
        return "redirect:/chat/" + id;
    }

    @PostMapping("/chat/{id}/organizar")
    public String organizar(@PathVariable Integer id, @RequestParam String estado,
            @RequestParam String prioridad, Principal principal, RedirectAttributes flash) {
        UsuarioEntity actual = usuario(principal);
        ConversacionEntity chat = conversaciones.findById(id).orElse(null);
        if (chat == null || !participa(chat, actual)) return inicio(actual);
        if (!chat.getVeterinario().getUsuario().getId().equals(actual.getId())) {
            flash.addFlashAttribute("errorPerfil", "Solo el veterinario puede organizar el estado y la prioridad de la consulta.");
            return "redirect:/chat/" + id;
        }
        if (!java.util.Set.of("PENDIENTE", "RESPONDIDA", "CERRADA").contains(estado)
                || !java.util.Set.of("NORMAL", "IMPORTANTE", "URGENTE").contains(prioridad)) {
            flash.addFlashAttribute("errorPerfil", "El estado o la prioridad no son válidos.");
        } else {
            chat.setEstado(estado); chat.setPrioridad(prioridad); conversaciones.save(chat);
            flash.addFlashAttribute("successChat", "Consulta actualizada.");
        }
        return "redirect:/chat/" + id;
    }

    @GetMapping("/chat/{id}/actividad")
    @ResponseBody
    public java.util.Map<String, Object> actividad(@PathVariable Integer id, Principal principal) {
        UsuarioEntity actual = usuario(principal);
        ConversacionEntity chat = conversaciones.findById(id).orElseThrow();
        if (!participa(chat, actual)) throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN);
        return java.util.Map.of(
                "mensajes", mensajes.countByConversacion(chat),
                "propiosSinLeer", mensajes.countByConversacionAndAutorAndLeidoFalse(chat, actual));
    }

    private boolean participa(ConversacionEntity c, UsuarioEntity u) {
        return c.getDueno().getId().equals(u.getId()) || c.getVeterinario().getUsuario().getId().equals(u.getId());
    }
    private UsuarioEntity usuario(Principal p) { return usuarios.findByEmail(p.getName()).orElseThrow(); }
    private String inicio(UsuarioEntity u) { return "VETERINARIO".equals(u.getTipoPerfil()) ? "redirect:/veterinario/home" : "redirect:/dueno/home"; }
}
