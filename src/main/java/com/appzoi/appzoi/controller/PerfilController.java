package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.model.MascotaEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.model.VeterinarioPerfilEntity;
import com.appzoi.appzoi.repository.MascotaRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;
import com.appzoi.appzoi.service.AlmacenamientoDocumentoService;
import com.appzoi.appzoi.service.AlmacenamientoImagenService;
import com.appzoi.appzoi.util.Validaciones;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private static final java.util.List<String> ESPECIES_PERMITIDAS = java.util.List.of(
            "Perros", "Gatos", "Conejos", "Hámsters", "Cobayos (cuyes)", "Chinchillas",
            "Jerbos", "Hurones", "Erizos pigmeos africanos", "Canarios",
            "Periquitos australianos", "Ninfas (carolinas)", "Agapornis",
            "Peces de acuario (como Betta o Goldfish)"
    );
    private static final java.util.List<String> LOCALIDADES_BOGOTA = java.util.List.of(
            "Antonio Nariño", "Barrios Unidos", "Bosa", "Chapinero", "Ciudad Bolívar",
            "Engativá", "Fontibón", "Kennedy", "La Candelaria", "Los Mártires",
            "Puente Aranda", "Rafael Uribe Uribe", "San Cristóbal", "Santa Fe",
            "Suba", "Sumapaz", "Teusaquillo", "Tunjuelito", "Usaquén", "Usme"
    );

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final UsuarioRepositorio usuarioRepository;
    private final MascotaRepositorio mascotaRepository;
    private final VeterinarioPerfilRepositorio veterinarioPerfilRepository;
    private final AlmacenamientoImagenService almacenamientoImagenService;
    private final AlmacenamientoDocumentoService almacenamientoDocumentoService;
    private final com.appzoi.appzoi.repository.ConversacionRepositorio conversacionRepository;
    private final com.appzoi.appzoi.repository.MensajeRepositorio mensajeRepository;
    private final com.appzoi.appzoi.repository.RecordatorioRepositorio recordatorioRepository;

    public PerfilController(UsuarioRepositorio usuarioRepository, MascotaRepositorio mascotaRepository,
            VeterinarioPerfilRepositorio veterinarioPerfilRepository,
            AlmacenamientoImagenService almacenamientoImagenService,
            AlmacenamientoDocumentoService almacenamientoDocumentoService,
            com.appzoi.appzoi.repository.ConversacionRepositorio conversacionRepository,
            com.appzoi.appzoi.repository.MensajeRepositorio mensajeRepository,
            com.appzoi.appzoi.repository.RecordatorioRepositorio recordatorioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioPerfilRepository = veterinarioPerfilRepository;
        this.almacenamientoImagenService = almacenamientoImagenService;
        this.almacenamientoDocumentoService = almacenamientoDocumentoService;
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.recordatorioRepository = recordatorioRepository;
    }

    @GetMapping("/seleccionar-perfil")
    public String seleccionarPerfil() {
        return "seleccionar_perfil";
    }

    @PostMapping("/seleccionar-perfil")
    public String guardarTipoPerfil(
            @RequestParam String tipoPerfil,
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        if (!"DUENO".equals(tipoPerfil) && !"VETERINARIO".equals(tipoPerfil)) {
            redirectAttributes.addFlashAttribute("errorPerfil", "Selecciona un perfil valido.");
            return "redirect:/seleccionar-perfil";
        }

        UsuarioEntity usuario = obtenerUsuario(principal);
        usuario.setTipoPerfil(tipoPerfil);
        usuarioRepository.save(usuario);
        actualizarRolAutenticado(tipoPerfil, request, response);

        if ("VETERINARIO".equals(tipoPerfil)) {
            return "redirect:/veterinario/perfil";
        }
        return "redirect:/mascotas/nueva";
    }

    @GetMapping("/mascotas/nueva")
    public String nuevaMascota(Model model) {
        model.addAttribute("mascota", new MascotaEntity());
        model.addAttribute("modoEdicion", false);
        model.addAttribute("especies", ESPECIES_PERMITIDAS);
        return "mascota_form";
    }

    @GetMapping("/mascotas/{id}/editar")
    public String editarMascota(
            @PathVariable Integer id,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UsuarioEntity dueno = obtenerUsuario(principal);
        MascotaEntity mascota = mascotaRepository.findByIdAndDueno(id, dueno).orElse(null);
        if (mascota == null) {
            redirectAttributes.addFlashAttribute("errorPerfil", "La mascota no existe o no te pertenece.");
            return "redirect:/dueno/home";
        }
        model.addAttribute("mascota", mascota);
        model.addAttribute("modoEdicion", true);
        model.addAttribute("especies", ESPECIES_PERMITIDAS);
        return "mascota_form";
    }

    @GetMapping("/dueno/mascotas/{id}")
    public String perfilMascota(
            @PathVariable Integer id,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        UsuarioEntity dueno = obtenerUsuario(principal);
        MascotaEntity mascota = mascotaRepository.findByIdAndDueno(id, dueno).orElse(null);
        if (mascota == null) {
            redirectAttributes.addFlashAttribute("errorPerfil", "La mascota no existe o no te pertenece.");
            return "redirect:/dueno/home";
        }
        model.addAttribute("mascota", mascota);
        model.addAttribute("conversaciones", conversacionRepository.findByDuenoAndMascotaOrderByActualizadaEnDesc(dueno, mascota));
        model.addAttribute("recordatorios", recordatorioRepository.findByMascotaOrderByCompletadoAscFechaHoraAsc(mascota));
        model.addAttribute("ahora", java.time.LocalDateTime.now());
        return "mascota_perfil";
    }

    @PostMapping("/mascotas")
    public String crearMascota(
            @RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam(required = false) String tipoMascota,
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String tipoSangre,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String esterilizado,
            @RequestParam(required = false) MultipartFile foto,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String vacunas,
            @RequestParam(required = false) MultipartFile carnetVacunacion,
            @RequestParam(defaultValue = "false") boolean vacunasNoVigentes,
            @RequestParam(required = false) String enfermedades,
            @RequestParam(required = false) String antecedentes,
            @RequestParam(required = false) String diagnostico,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String errorMascota = validarMascota(nombre, especie, tipoMascota, raza, tipoSangre,
                sexo, fechaNacimiento, edad, esterilizado, descripcion, vacunas,
                enfermedades, antecedentes, diagnostico);
        if (errorMascota != null) {
            redirectAttributes.addFlashAttribute("errorPerfil", errorMascota);
            return "redirect:/mascotas/nueva";
        }
        if ((carnetVacunacion == null || carnetVacunacion.isEmpty()) && !vacunasNoVigentes) {
            redirectAttributes.addFlashAttribute("errorPerfil", "Sube una foto del carné de vacunación o indica que las vacunas no están vigentes.");
            return "redirect:/mascotas/nueva";
        }

        UsuarioEntity dueno = obtenerUsuario(principal);
        dueno.setTipoPerfil("DUENO");
        usuarioRepository.save(dueno);

        String fotoUrl;
        String carnetVacunacionUrl;
        try {
            fotoUrl = almacenamientoImagenService.guardar(foto, "mascotas");
            carnetVacunacionUrl = almacenamientoImagenService.guardar(carnetVacunacion, "carnets-vacunacion");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorPerfil", exception.getMessage());
            return "redirect:/mascotas/nueva";
        }

        MascotaEntity mascota = new MascotaEntity();
        completarMascota(mascota, nombre, especie, tipoMascota, raza, tipoSangre, sexo,
                fechaNacimiento, edad, esterilizado, fotoUrl, descripcion, vacunas,
                enfermedades, antecedentes, diagnostico);
        mascota.setCarnetVacunacionUrl(carnetVacunacionUrl);
        mascota.setVacunasVigentes(!vacunasNoVigentes);
        mascota.setDueno(dueno);

        mascotaRepository.save(mascota);
        redirectAttributes.addFlashAttribute("successPerfil", "Mascota creada correctamente.");
        return "redirect:/dueno/home";
    }

    @PostMapping("/mascotas/{id}")
    public String actualizarMascota(
            @PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam(required = false) String tipoMascota,
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) String tipoSangre,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String fechaNacimiento,
            @RequestParam(required = false) Integer edad,
            @RequestParam(required = false) String esterilizado,
            @RequestParam(required = false) MultipartFile foto,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String vacunas,
            @RequestParam(required = false) MultipartFile carnetVacunacion,
            @RequestParam(defaultValue = "false") boolean vacunasNoVigentes,
            @RequestParam(required = false) String enfermedades,
            @RequestParam(required = false) String antecedentes,
            @RequestParam(required = false) String diagnostico,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String errorMascota = validarMascota(nombre, especie, tipoMascota, raza, tipoSangre,
                sexo, fechaNacimiento, edad, esterilizado, descripcion, vacunas,
                enfermedades, antecedentes, diagnostico);
        if (errorMascota != null) {
            redirectAttributes.addFlashAttribute("errorPerfil", errorMascota);
            return "redirect:/mascotas/" + id + "/editar";
        }

        UsuarioEntity dueno = obtenerUsuario(principal);
        MascotaEntity mascota = mascotaRepository.findByIdAndDueno(id, dueno).orElse(null);
        if (mascota == null) {
            redirectAttributes.addFlashAttribute("errorPerfil", "La mascota no existe o no te pertenece.");
            return "redirect:/dueno/home";
        }
        if ((carnetVacunacion == null || carnetVacunacion.isEmpty())
                && mascota.getCarnetVacunacionUrl() == null && !vacunasNoVigentes) {
            redirectAttributes.addFlashAttribute("errorPerfil", "Sube una foto del carné de vacunación o indica que las vacunas no están vigentes.");
            return "redirect:/mascotas/" + id + "/editar";
        }

        String fotoAnterior = mascota.getFotoUrl();
        String fotoUrl = fotoAnterior;
        String carnetAnterior = mascota.getCarnetVacunacionUrl();
        String carnetUrl = carnetAnterior;
        try {
            String nuevaFoto = almacenamientoImagenService.guardar(foto, "mascotas");
            if (nuevaFoto != null) {
                fotoUrl = nuevaFoto;
            }
            String nuevoCarnet = almacenamientoImagenService.guardar(carnetVacunacion, "carnets-vacunacion");
            if (nuevoCarnet != null) carnetUrl = nuevoCarnet;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorPerfil", exception.getMessage());
            return "redirect:/mascotas/" + id + "/editar";
        }

        completarMascota(mascota, nombre, especie, tipoMascota, raza, tipoSangre, sexo,
                fechaNacimiento, edad, esterilizado, fotoUrl, descripcion, vacunas,
                enfermedades, antecedentes, diagnostico);
        mascota.setCarnetVacunacionUrl(carnetUrl);
        mascota.setVacunasVigentes(!vacunasNoVigentes);
        mascotaRepository.save(mascota);
        if (!java.util.Objects.equals(fotoAnterior, fotoUrl)) {
            almacenamientoImagenService.eliminar(fotoAnterior);
        }
        if (!java.util.Objects.equals(carnetAnterior, carnetUrl)) almacenamientoImagenService.eliminar(carnetAnterior);
        redirectAttributes.addFlashAttribute("successPerfil", "Mascota actualizada correctamente.");
        return "redirect:/dueno/home";
    }

    @PostMapping("/mascotas/{id}/eliminar")
    @org.springframework.transaction.annotation.Transactional
    public String eliminarMascota(
            @PathVariable Integer id,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        UsuarioEntity dueno = obtenerUsuario(principal);
        MascotaEntity mascota = mascotaRepository.findById(id).orElse(null);
        if (mascota == null || !mascota.getDueno().getId().equals(dueno.getId())) {
            redirectAttributes.addFlashAttribute("errorPerfil", "No se pudo eliminar esa mascota.");
            return "redirect:/dueno/home";
        }

        String fotoUrl = mascota.getFotoUrl();
        String carnetVacunacionUrl = mascota.getCarnetVacunacionUrl();
        java.util.List<com.appzoi.appzoi.model.ConversacionEntity> conversaciones =
                conversacionRepository.findByMascota(mascota);
        if (!conversaciones.isEmpty()) {
            mensajeRepository.deleteByConversacionIn(conversaciones);
            conversacionRepository.deleteAll(conversaciones);
        }
        recordatorioRepository.deleteByMascota(mascota);
        mascotaRepository.delete(mascota);
        almacenamientoImagenService.eliminar(fotoUrl);
        almacenamientoImagenService.eliminar(carnetVacunacionUrl);
        redirectAttributes.addFlashAttribute("successPerfil", "Mascota eliminada correctamente.");
        return "redirect:/dueno/home";
    }

    @GetMapping("/veterinario/perfil")
    public String perfilVeterinario(Model model, Principal principal) {
        UsuarioEntity usuario = obtenerUsuario(principal);
        VeterinarioPerfilEntity perfil = veterinarioPerfilRepository.findByUsuario(usuario)
                .orElseGet(VeterinarioPerfilEntity::new);
        model.addAttribute("perfil", perfil);
        model.addAttribute("localidades", LOCALIDADES_BOGOTA);
        return "veterinario_perfil";
    }

    @PostMapping("/veterinario/perfil")
    public String guardarPerfilVeterinario(
            @RequestParam String especialidad,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) String experiencia,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) MultipartFile foto,
            @RequestParam(required = false) MultipartFile tituloPdf,
            @RequestParam(required = false) String tarjetaProfesional,
            @RequestParam(required = false) String clinica,
            @RequestParam String localidad,
            @RequestParam(required = false) String telefono,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        String errorVeterinario = validarVeterinario(especialidad, numeroDocumento, experiencia,
                descripcion, tarjetaProfesional, clinica, localidad, telefono);
        if (errorVeterinario != null) {
            redirectAttributes.addFlashAttribute("errorPerfil", errorVeterinario);
            return "redirect:/veterinario/perfil";
        }

        UsuarioEntity usuario = obtenerUsuario(principal);
        usuario.setTipoPerfil("VETERINARIO");
        usuarioRepository.save(usuario);

        VeterinarioPerfilEntity perfil = veterinarioPerfilRepository.findByUsuario(usuario)
                .orElseGet(VeterinarioPerfilEntity::new);
        if ((perfil.getFotoUrl() == null || perfil.getFotoUrl().isBlank()) && (foto == null || foto.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorPerfil", "La foto profesional es obligatoria.");
            return "redirect:/veterinario/perfil";
        }
        if ((perfil.getTituloUrl() == null || perfil.getTituloUrl().isBlank()) && (tituloPdf == null || tituloPdf.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorPerfil", "El título o certificado PDF es obligatorio.");
            return "redirect:/veterinario/perfil";
        }
        perfil.setUsuario(usuario);
        perfil.setEspecialidad(especialidad);
        perfil.setNumeroDocumento(numeroDocumento);
        perfil.setExperiencia(experiencia);
        String fotoAnterior = perfil.getFotoUrl();
        String fotoUrl = fotoAnterior;
        try {
            String nuevaFoto = almacenamientoImagenService.guardar(foto, "veterinarios");
            if (nuevaFoto != null) {
                fotoUrl = nuevaFoto;
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorPerfil", exception.getMessage());
            return "redirect:/veterinario/perfil";
        }

        String tituloAnterior = perfil.getTituloUrl();
        String tituloUrl = tituloAnterior;
        try {
            String nuevoTitulo = almacenamientoDocumentoService.guardarPdf(
                    tituloPdf, "titulos-veterinarios"
            );
            if (nuevoTitulo != null) {
                tituloUrl = nuevoTitulo;
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            if (!java.util.Objects.equals(fotoAnterior, fotoUrl)) {
                almacenamientoImagenService.eliminar(fotoUrl);
            }
            redirectAttributes.addFlashAttribute("errorPerfil", exception.getMessage());
            return "redirect:/veterinario/perfil";
        }

        perfil.setDescripcion(descripcion);
        perfil.setFotoUrl(fotoUrl);
        perfil.setTituloUrl(tituloUrl);
        perfil.setTarjetaProfesional(tarjetaProfesional);
        perfil.setClinica(clinica);
        perfil.setLocalidad(localidad);
        perfil.setTelefono(telefono);
        veterinarioPerfilRepository.save(perfil);
        if (!java.util.Objects.equals(fotoAnterior, fotoUrl)) {
            almacenamientoImagenService.eliminar(fotoAnterior);
        }
        if (!java.util.Objects.equals(tituloAnterior, tituloUrl)) {
            almacenamientoDocumentoService.eliminar(tituloAnterior);
        }

        redirectAttributes.addFlashAttribute("successPerfil", "Perfil veterinario guardado correctamente.");
        return "redirect:/veterinario/mi-perfil";
    }

    private UsuarioEntity obtenerUsuario(Principal principal) {
        return usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }

    private String validarMascota(
            String nombre, String especie, String tipoMascota, String raza, String tipoSangre,
            String sexo, String fechaNacimiento, Integer edad, String esterilizado,
            String descripcion, String vacunas, String enfermedades, String antecedentes,
            String diagnostico
    ) {
        if (!Validaciones.texto(nombre)) return "El nombre debe tener entre 2 y 80 letras.";
        if (especie == null || !ESPECIES_PERMITIDAS.contains(especie)) return "Selecciona una especie válida de la lista.";
        if (!Validaciones.texto(tipoMascota)) return "El tipo de mascota es obligatorio y solo admite letras y espacios.";
        if (!Validaciones.textoOpcional(raza)) return "La raza solo admite letras y espacios.";
        if (!Validaciones.longitudMaxima(tipoSangre, 30)) return "El tipo de sangre no puede superar 30 caracteres.";
        if (!"Hembra".equals(sexo) && !"Macho".equals(sexo)) {
            return "Selecciona un sexo válido.";
        }
        if (!Validaciones.fechaNoFutura(fechaNacimiento)) return "La fecha de nacimiento no puede ser futura.";
        if (!Validaciones.enteroEnRango(edad, 0, 80)) return "La edad debe estar entre 0 y 80 años.";
        if ((fechaNacimiento == null || fechaNacimiento.isBlank()) && edad == null) return "Registra la fecha de nacimiento o la edad de la mascota.";
        if (!"Si".equals(esterilizado) && !"No".equals(esterilizado)) {
            return "Selecciona un estado de esterilización válido.";
        }
        if (!Validaciones.longitudMaxima(descripcion, 600)) return "La descripción no puede superar 600 caracteres.";
        if (!Validaciones.longitudMaxima(vacunas, 600)) return "Las vacunas no pueden superar 600 caracteres.";
        if (!Validaciones.longitudMaxima(enfermedades, 600)) return "Las enfermedades no pueden superar 600 caracteres.";
        if (!Validaciones.longitudMaxima(antecedentes, 800)) return "Los antecedentes no pueden superar 800 caracteres.";
        if (!Validaciones.longitudMaxima(diagnostico, 800)) return "El diagnóstico no puede superar 800 caracteres.";
        return null;
    }

    private String validarVeterinario(
            String especialidad, String numeroDocumento, String experiencia, String descripcion,
            String tarjetaProfesional, String clinica, String localidad, String telefono
    ) {
        if (!Validaciones.texto(especialidad)) return "La especialidad debe tener entre 2 y 80 letras.";
        if (numeroDocumento == null || !numeroDocumento.trim().matches("[0-9]{5,30}")) return "El documento es obligatorio y debe contener entre 5 y 30 números.";
        if (experiencia == null || experiencia.isBlank() || experiencia.length() > 120) return "La experiencia es obligatoria y no puede superar 120 caracteres.";
        if (descripcion == null || descripcion.isBlank() || descripcion.length() > 800) return "La descripción profesional es obligatoria y no puede superar 800 caracteres.";
        if (tarjetaProfesional == null || tarjetaProfesional.isBlank() || !Validaciones.tarjetaProfesional(tarjetaProfesional)) return "La tarjeta profesional es obligatoria.";
        if (!Validaciones.texto(clinica)) return "El nombre de la clínica es obligatorio y solo admite letras y espacios.";
        if (localidad == null || !LOCALIDADES_BOGOTA.contains(localidad)) return "Selecciona una localidad válida de Bogotá.";
        if (!Validaciones.telefonoObligatorio(telefono)) return "El teléfono es obligatorio y debe tener entre 7 y 20 caracteres válidos.";
        return null;
    }

    private void completarMascota(
            MascotaEntity mascota,
            String nombre,
            String especie,
            String tipoMascota,
            String raza,
            String tipoSangre,
            String sexo,
            String fechaNacimiento,
            Integer edad,
            String esterilizado,
            String fotoUrl,
            String descripcion,
            String vacunas,
            String enfermedades,
            String antecedentes,
            String diagnostico
    ) {
        mascota.setNombre(nombre.trim());
        mascota.setEspecie(especie.trim());
        mascota.setTipoMascota(tipoMascota);
        mascota.setRaza(raza);
        mascota.setTipoSangre(tipoSangre);
        mascota.setSexo(sexo);
        LocalDate nacimiento = fechaNacimiento == null || fechaNacimiento.isBlank()
                ? null : LocalDate.parse(fechaNacimiento);
        mascota.setEdad(nacimiento == null ? edad : java.time.Period.between(nacimiento, LocalDate.now()).getYears());
        mascota.setEsterilizado(esterilizado);
        mascota.setFotoUrl(fotoUrl);
        mascota.setDescripcion(descripcion);
        mascota.setVacunas(vacunas);
        mascota.setEnfermedades(enfermedades);
        mascota.setAntecedentes(antecedentes);
        mascota.setDiagnostico(diagnostico);
        mascota.setFechaNacimiento(nacimiento);
    }

    private void actualizarRolAutenticado(
            String tipoPerfil,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication actual = SecurityContextHolder.getContext().getAuthentication();
        Authentication actualizado = new UsernamePasswordAuthenticationToken(
                actual.getPrincipal(),
                actual.getCredentials(),
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + tipoPerfil))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(actualizado);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}
