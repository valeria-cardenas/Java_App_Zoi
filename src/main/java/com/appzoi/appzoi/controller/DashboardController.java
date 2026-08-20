package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.CalificacionVeterinarioEntity;
import com.appzoi.appzoi.model.ConversacionEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.model.VeterinarioPerfilEntity;
import com.appzoi.appzoi.repository.CalificacionVeterinarioRepositorio;
import com.appzoi.appzoi.repository.ConversacionRepositorio;
import com.appzoi.appzoi.repository.MascotaRepositorio;
import com.appzoi.appzoi.repository.RecordatorioRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final UsuarioRepositorio usuarios;
    private final MascotaRepositorio mascotas;
    private final VeterinarioPerfilRepositorio veterinarios;
    private final ConversacionRepositorio conversaciones;
    private final RecordatorioRepositorio recordatorios;
    private final CalificacionVeterinarioRepositorio calificaciones;

    public DashboardController(UsuarioRepositorio usuarios, MascotaRepositorio mascotas,
            VeterinarioPerfilRepositorio veterinarios, ConversacionRepositorio conversaciones,
            RecordatorioRepositorio recordatorios, CalificacionVeterinarioRepositorio calificaciones) {
        this.usuarios = usuarios;
        this.mascotas = mascotas;
        this.veterinarios = veterinarios;
        this.conversaciones = conversaciones;
        this.recordatorios = recordatorios;
        this.calificaciones = calificaciones;
    }

    @GetMapping("/dueno/home")
    public String homeDueno(Model model, Principal principal) {
        UsuarioEntity dueno = usuario(principal);
        model.addAttribute("usuario", dueno);
        model.addAttribute("mascotas", mascotas.findByDueno(dueno));
        model.addAttribute("totalMascotas", mascotas.countByDueno(dueno));
        model.addAttribute("conversaciones", conversaciones.findByDuenoOrderByActualizadaEnDesc(dueno));
        model.addAttribute("recordatoriosVencidos",
                recordatorios.countByMascotaDuenoAndCompletadoFalseAndFechaHoraBefore(dueno, LocalDateTime.now()));
        return "dueno_home";
    }

    @GetMapping("/veterinario/home")
    public String homeVeterinario(Model model, Principal principal) {
        UsuarioEntity usuario = usuario(principal);
        VeterinarioPerfilEntity perfil = veterinarios.findByUsuario(usuario).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        if (perfil != null) cargarResumenVeterinario(model, perfil);
        return "veterinario_home";
    }

    @GetMapping("/veterinario/consultas")
    public String consultasVeterinario(Model model, Principal principal) {
        UsuarioEntity usuario = usuario(principal);
        VeterinarioPerfilEntity perfil = veterinarios.findByUsuario(usuario).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        if (perfil == null) {
            model.addAttribute("pendientes", List.of());
            model.addAttribute("atendidas", List.of());
            return "veterinario_consultas";
        }
        List<ConversacionEntity> lista =
                conversaciones.findByVeterinarioOrderByPendienteVeterinarioDescActualizadaEnDesc(perfil);
        model.addAttribute("pendientes", lista.stream().filter(ConversacionEntity::isPendienteVeterinario).toList());
        model.addAttribute("atendidas", lista.stream().filter(c -> !c.isPendienteVeterinario()).toList());
        model.addAttribute("totalConsultas", lista.size());
        model.addAttribute("totalMascotas", lista.stream().map(c -> c.getMascota().getId()).distinct().count());
        return "veterinario_consultas";
    }

    private void cargarResumenVeterinario(Model model, VeterinarioPerfilEntity perfil) {
        List<ConversacionEntity> lista =
                conversaciones.findByVeterinarioOrderByPendienteVeterinarioDescActualizadaEnDesc(perfil);
        List<CalificacionVeterinarioEntity> opiniones =
                calificaciones.findByVeterinarioOrderByActualizadaEnDesc(perfil);
        model.addAttribute("conversaciones", lista);
        model.addAttribute("pendientes", conversaciones.countByVeterinarioAndPendienteVeterinarioTrue(perfil));
        model.addAttribute("totalConsultas", lista.size());
        model.addAttribute("totalMascotas", lista.stream().map(c -> c.getMascota().getId()).distinct().count());
        model.addAttribute("consultasSemana", lista.stream()
                .filter(c -> c.getActualizadaEn() != null && c.getActualizadaEn().isAfter(LocalDateTime.now().minusDays(7)))
                .count());
        model.addAttribute("recientes", lista.stream().limit(3).toList());
        model.addAttribute("proximaConsulta", lista.stream().filter(ConversacionEntity::isPendienteVeterinario)
                .min(Comparator.comparing(ConversacionEntity::getActualizadaEn)).orElse(null));
        model.addAttribute("promedio", calificaciones.promedio(perfil));
        model.addAttribute("totalCalificaciones", opiniones.size());
        model.addAttribute("ultimaCalificacion", opiniones.isEmpty() ? null : opiniones.get(0));
        model.addAttribute("porcentajePerfil", porcentajePerfil(perfil));
    }

    private int porcentajePerfil(VeterinarioPerfilEntity perfil) {
        List<String> datos = Arrays.asList(perfil.getEspecialidad(), perfil.getNumeroDocumento(),
                perfil.getExperiencia(), perfil.getDescripcion(), perfil.getFotoUrl(), perfil.getTituloUrl(),
                perfil.getTarjetaProfesional(), perfil.getClinica(), perfil.getLocalidad(), perfil.getTelefono());
        return (int) (datos.stream().filter(dato -> dato != null && !dato.isBlank()).count() * 100 / datos.size());
    }

    private UsuarioEntity usuario(Principal principal) {
        return usuarios.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }
}
