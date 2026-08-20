package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.util.Validaciones;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
public class RegistroController {

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepositorio usuarioRepository;

    public RegistroController(PasswordEncoder passwordEncoder, UsuarioRepositorio usuarioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @InitBinder("usuario")
    void protegerCamposInternos(WebDataBinder binder) {
        binder.setDisallowedFields("id", "tipoPerfil");
    }

    @PostMapping("/registro")
    public String registrarUsuario(
            @ModelAttribute("usuario") UsuarioEntity usuario,
            @RequestParam String confirmPassword,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        Map<String, String> errores = validarRegistro(usuario, confirmPassword);
        if (!errores.isEmpty()) {
            prepararError(usuario, errores, redirectAttributes);
            return "redirect:/registro";
        }

        usuario.setNombre(usuario.getNombre().trim());
        usuario.setApellido(usuario.getApellido().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setTelefono(usuario.getTelefono().trim());

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            errores.put("email", "Ya existe una cuenta registrada con este correo.");
            prepararError(usuario, errores, redirectAttributes);
            return "redirect:/registro";
        }

        String claveEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(claveEncriptada);
        usuario.setId(null);
        usuario.setTipoPerfil(null);

        UsuarioEntity usuarioGuardado;
        try {
            usuarioGuardado = usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException exception) {
            errores.put("email", "Ya existe una cuenta registrada con este correo.");
            prepararError(usuario, errores, redirectAttributes);
            return "redirect:/registro";
        }
        autenticarUsuario(usuarioGuardado, request, response);

        return "redirect:/seleccionar-perfil";
    }

    private Map<String, String> validarRegistro(UsuarioEntity usuario, String confirmPassword) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (!Validaciones.texto(usuario.getNombre())) {
            errores.put("nombre", "Escribe un nombre de 2 a 80 caracteres usando solo letras y espacios.");
        }
        if (!Validaciones.texto(usuario.getApellido())) {
            errores.put("apellido", "Escribe un apellido de 2 a 80 caracteres usando solo letras y espacios.");
        }
        if (!Validaciones.emailRegistroPermitido(usuario.getEmail())) {
            errores.put("email", "Usa una dirección de Gmail (@gmail.com) o Outlook (@outlook.com).");
        }
        if (!Validaciones.telefonoObligatorio(usuario.getTelefono())) {
            errores.put("telefono", "Ingresa un teléfono válido de 7 a 20 caracteres.");
        }
        if (!Validaciones.password(usuario.getPassword())) {
            errores.put("password", "Usa entre 8 y 72 caracteres, con mayúscula, minúscula y número.");
        }
        if (usuario.getPassword() == null || !usuario.getPassword().equals(confirmPassword)) {
            errores.put("confirmPassword", "Las contraseñas no coinciden.");
        }
        return errores;
    }

    private void prepararError(
            UsuarioEntity usuario,
            Map<String, String> errores,
            RedirectAttributes redirectAttributes
    ) {
        usuario.setPassword(null);
        redirectAttributes.addFlashAttribute("usuario", usuario);
        redirectAttributes.addFlashAttribute("erroresRegistro", errores);
        redirectAttributes.addFlashAttribute(
                "errorRegistro",
                "Revisa los campos marcados antes de continuar."
        );
    }

    private void autenticarUsuario(
            UsuarioEntity usuario,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        UserDetails userDetails = User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles("USER")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
