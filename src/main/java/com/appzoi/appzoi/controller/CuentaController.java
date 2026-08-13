package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.config.AdminUserSeeder;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;
import com.appzoi.appzoi.repository.CalificacionVeterinarioRepositorio;
import com.appzoi.appzoi.service.EliminacionCuentaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.appzoi.appzoi.util.Validaciones;
import java.time.LocalDate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CuentaController {
    private final UsuarioRepositorio usuarios;
    private final EliminacionCuentaService eliminacion;
    private final PasswordEncoder passwordEncoder;
    private final VeterinarioPerfilRepositorio perfilesVeterinarios;
    private final CalificacionVeterinarioRepositorio calificaciones;

    public CuentaController(UsuarioRepositorio usuarios, EliminacionCuentaService eliminacion,
            PasswordEncoder passwordEncoder, VeterinarioPerfilRepositorio perfilesVeterinarios,
            CalificacionVeterinarioRepositorio calificaciones) {
        this.usuarios = usuarios; this.eliminacion = eliminacion; this.passwordEncoder = passwordEncoder;
        this.perfilesVeterinarios = perfilesVeterinarios;
        this.calificaciones = calificaciones;
    }

    @GetMapping("/veterinario/mi-perfil")
    public String perfilVeterinario(Principal principal, Model model) {
        UsuarioEntity usuario = usuario(principal);
        model.addAttribute("usuario", usuario);
        com.appzoi.appzoi.model.VeterinarioPerfilEntity perfil = perfilesVeterinarios.findByUsuario(usuario).orElse(null);
        model.addAttribute("perfil", perfil);
        if (perfil != null) { model.addAttribute("calificaciones", calificaciones.findByVeterinarioOrderByActualizadaEnDesc(perfil)); model.addAttribute("promedio", calificaciones.promedio(perfil)); model.addAttribute("totalCalificaciones", calificaciones.countByVeterinario(perfil)); }
        return "veterinario_cuenta";
    }

    @GetMapping("/veterinario/calificaciones")
    public String calificacionesVeterinario(Principal principal, Model model) {
        UsuarioEntity usuario = usuario(principal);
        com.appzoi.appzoi.model.VeterinarioPerfilEntity perfil = perfilesVeterinarios.findByUsuario(usuario).orElse(null);
        model.addAttribute("usuario", usuario); model.addAttribute("perfil", perfil);
        if (perfil != null) { model.addAttribute("calificaciones", calificaciones.findByVeterinarioOrderByActualizadaEnDesc(perfil)); model.addAttribute("promedio", calificaciones.promedio(perfil)); }
        return "veterinario_calificaciones";
    }

    @GetMapping("/dueno/perfil")
    public String perfil(Principal principal, Model model) {
        model.addAttribute("usuario", usuario(principal));
        return "dueno_perfil";
    }

    @PostMapping({"/dueno/perfil", "/veterinario/mi-perfil"})
    public String actualizarPerfil(@RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String fechaNacimiento, Principal principal,
            HttpServletRequest request, RedirectAttributes flash) throws ServletException {
        UsuarioEntity actual = usuario(principal);
        if (!Validaciones.texto(nombre)) return errorPerfil(flash, actual, "El nombre debe tener entre 2 y 80 letras.");
        if (!Validaciones.texto(apellido)) return errorPerfil(flash, actual, "El apellido debe tener entre 2 y 80 letras.");
        if (!Validaciones.email(email)) return errorPerfil(flash, actual, "Ingresa un correo electrónico válido.");
        if (!Validaciones.telefono(telefono)) return errorPerfil(flash, actual, "El teléfono debe tener entre 7 y 20 caracteres válidos.");
        if (!Validaciones.fechaNoFutura(fechaNacimiento)) return errorPerfil(flash, actual, "La fecha de nacimiento no puede ser futura.");
        UsuarioEntity mismoCorreo = usuarios.findByEmail(email.trim()).orElse(null);
        if (mismoCorreo != null && !mismoCorreo.getId().equals(actual.getId())) {
            flash.addFlashAttribute("errorPerfil", "Ese correo ya está registrado.");
            return redireccionPerfil(actual);
        }
        boolean cambioCorreo = !actual.getEmail().equalsIgnoreCase(email.trim());
        actual.setNombre(nombre.trim()); actual.setApellido(apellido.trim());
        actual.setEmail(email.trim()); actual.setTelefono(telefono);
        actual.setFechaNacimiento(fechaNacimiento == null || fechaNacimiento.isBlank() ? null : LocalDate.parse(fechaNacimiento));
        usuarios.save(actual);
        if (cambioCorreo) {
            request.logout();
            if (request.getSession(false) != null) request.getSession(false).invalidate();
            return "redirect:/login?perfilActualizado=true";
        }
        flash.addFlashAttribute("successPerfil", "Perfil actualizado correctamente.");
        return redireccionPerfil(actual);
    }

    @PostMapping({"/dueno/perfil/contrasena", "/veterinario/mi-perfil/contrasena"})
    public String cambiarContrasena(@RequestParam String passwordActual,
            @RequestParam String passwordNueva, @RequestParam String confirmarPassword,
            Principal principal, RedirectAttributes flash) {
        UsuarioEntity actual = usuario(principal);
        if (!passwordEncoder.matches(passwordActual, actual.getPassword())) {
            flash.addFlashAttribute("errorPassword", "La contraseña actual no es correcta.");
        } else if (!Validaciones.password(passwordNueva)) {
            flash.addFlashAttribute("errorPassword", "La nueva contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula y número.");
        } else if (!passwordNueva.equals(confirmarPassword)) {
            flash.addFlashAttribute("errorPassword", "Las contraseñas nuevas no coinciden.");
        } else {
            actual.setPassword(passwordEncoder.encode(passwordNueva)); usuarios.save(actual);
            flash.addFlashAttribute("successPassword", "Contraseña actualizada correctamente.");
        }
        return redireccionPerfil(actual) + "#seguridad";
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarCuentaPropia(@RequestParam String password, Principal principal, HttpServletRequest request,
            RedirectAttributes flash) throws ServletException {
        UsuarioEntity usuario = usuarios.findByEmail(principal.getName()).orElse(null);
        if (usuario == null) return "redirect:/login";
        if (AdminUserSeeder.ADMIN_EMAIL.equalsIgnoreCase(usuario.getEmail())) {
            flash.addFlashAttribute("errorAdmin", "El administrador principal no puede eliminar su cuenta.");
            return "redirect:/admin";
        }
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            flash.addFlashAttribute("errorEliminar", "La contraseña no es correcta. La cuenta no fue eliminada.");
            return "DUENO".equals(usuario.getTipoPerfil()) ? "redirect:/dueno/perfil#eliminar" : "redirect:/veterinario/mi-perfil#eliminar";
        }
        eliminacion.eliminar(usuario);
        request.logout();
        if (request.getSession(false) != null) request.getSession(false).invalidate();
        return "redirect:/login?cuentaEliminada=true";
    }

    private UsuarioEntity usuario(Principal principal) {
        return usuarios.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }

    private String redireccionPerfil(UsuarioEntity usuario) {
        return "VETERINARIO".equals(usuario.getTipoPerfil())
                ? "redirect:/veterinario/mi-perfil" : "redirect:/dueno/perfil";
    }

    private String errorPerfil(RedirectAttributes flash, UsuarioEntity usuario, String mensaje) {
        flash.addFlashAttribute("errorPerfil", mensaje);
        return redireccionPerfil(usuario);
    }
}
