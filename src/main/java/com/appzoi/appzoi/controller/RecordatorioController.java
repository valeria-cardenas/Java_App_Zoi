package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.*;
import com.appzoi.appzoi.repository.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dueno/recordatorios")
public class RecordatorioController {
    private static final Set<String> TIPOS = Set.of("ALIMENTACION", "BANO", "MEDICAMENTO", "CITA", "OTRO");
    private static final Set<String> REPETICIONES = Set.of("NINGUNA", "DIARIA", "SEMANAL", "MENSUAL");
    private final RecordatorioRepositorio recordatorios;
    private final MascotaRepositorio mascotas;
    private final UsuarioRepositorio usuarios;

    public RecordatorioController(RecordatorioRepositorio recordatorios, MascotaRepositorio mascotas, UsuarioRepositorio usuarios) {
        this.recordatorios = recordatorios; this.mascotas = mascotas; this.usuarios = usuarios;
    }

    @GetMapping
    public String lista(Model model, Principal principal) {
        UsuarioEntity dueno = usuario(principal);
        model.addAttribute("recordatorios", recordatorios.findByMascotaDuenoAndCompletadoFalseOrderByFechaHoraAsc(dueno));
        model.addAttribute("ahora", LocalDateTime.now());
        return "recordatorios";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(required = false) Integer mascotaId, Model model, Principal principal) {
        cargarFormulario(model, usuario(principal), new RecordatorioEntity(), mascotaId);
        return "recordatorio_form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, Principal principal, RedirectAttributes flash) {
        RecordatorioEntity r = propio(id, usuario(principal));
        if (r == null) return error(flash);
        cargarFormulario(model, usuario(principal), r, r.getMascota().getId());
        return "recordatorio_form";
    }

    @PostMapping
    public String guardar(@RequestParam(required = false) Integer id, @RequestParam Integer mascotaId,
            @RequestParam String tipo, @RequestParam String titulo, @RequestParam(required = false) String descripcion,
            @RequestParam LocalDateTime fechaHora, @RequestParam String repeticion,
            Principal principal, RedirectAttributes flash) {
        UsuarioEntity dueno = usuario(principal);
        MascotaEntity mascota = mascotas.findByIdAndDueno(mascotaId, dueno).orElse(null);
        RecordatorioEntity r = id == null ? new RecordatorioEntity() : propio(id, dueno);
        if (mascota == null || r == null) return error(flash);
        String t = titulo == null ? "" : titulo.trim();
        String destino = id == null ? "redirect:/dueno/recordatorios/nuevo"
                : "redirect:/dueno/recordatorios/" + id + "/editar";
        if (!TIPOS.contains(tipo)) return errorFormulario(flash, destino, "Selecciona un tipo de cuidado válido.");
        if (!REPETICIONES.contains(repeticion)) return errorFormulario(flash, destino, "Selecciona una repetición válida.");
        if (t.length() < 2 || t.length() > 120) return errorFormulario(flash, destino, "El título debe tener entre 2 y 120 caracteres.");
        if (descripcion != null && descripcion.length() > 600) return errorFormulario(flash, destino, "Las notas no pueden superar 600 caracteres.");
        r.setMascota(mascota); r.setTipo(tipo); r.setTitulo(t); r.setDescripcion(descripcion);
        r.setFechaHora(fechaHora); r.setRepeticion(repeticion); recordatorios.save(r);
        flash.addFlashAttribute("successPerfil", "Recordatorio guardado correctamente.");
        return "redirect:/dueno/recordatorios";
    }

    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Integer id, Principal principal, RedirectAttributes flash) {
        RecordatorioEntity r = propio(id, usuario(principal));
        if (r == null) return error(flash);
        if (!"NINGUNA".equals(r.getRepeticion())) {
            r.setCompletado(true);
            RecordatorioEntity siguiente = new RecordatorioEntity();
            siguiente.setMascota(r.getMascota()); siguiente.setTipo(r.getTipo());
            siguiente.setTitulo(r.getTitulo()); siguiente.setDescripcion(r.getDescripcion());
            siguiente.setRepeticion(r.getRepeticion());
            siguiente.setFechaHora(siguiente(r.getFechaHora(), r.getRepeticion()));
            recordatorios.save(siguiente);
            flash.addFlashAttribute("successPerfil", "Tarea realizada. La siguiente repetición ya está en el calendario.");
        } else { r.setCompletado(true); flash.addFlashAttribute("successPerfil", "Recordatorio completado."); }
        recordatorios.save(r); return "redirect:/dueno/recordatorios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, Principal principal, RedirectAttributes flash) {
        RecordatorioEntity r = propio(id, usuario(principal));
        if (r == null) return error(flash);
        recordatorios.delete(r); flash.addFlashAttribute("successPerfil", "Recordatorio eliminado.");
        return "redirect:/dueno/recordatorios";
    }

    private LocalDateTime siguiente(LocalDateTime fecha, String repeticion) {
        LocalDateTime base = fecha.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : fecha;
        return switch (repeticion) { case "DIARIA" -> base.plusDays(1); case "SEMANAL" -> base.plusWeeks(1); default -> base.plusMonths(1); };
    }
    private RecordatorioEntity propio(Integer id, UsuarioEntity dueno) { return recordatorios.findById(id).filter(r -> r.getMascota().getDueno().getId().equals(dueno.getId())).orElse(null); }
    private UsuarioEntity usuario(Principal p) { return usuarios.findByEmail(p.getName()).orElseThrow(); }
    private String error(RedirectAttributes f) { f.addFlashAttribute("errorPerfil", "Recordatorio no encontrado."); return "redirect:/dueno/recordatorios"; }
    private String errorFormulario(RedirectAttributes f, String destino, String mensaje) { f.addFlashAttribute("errorPerfil", mensaje); return destino; }
    private void cargarFormulario(Model m, UsuarioEntity d, RecordatorioEntity r, Integer mascotaId) { m.addAttribute("recordatorio", r); m.addAttribute("mascotas", mascotas.findByDueno(d)); m.addAttribute("mascotaSeleccionada", mascotaId); }
}
