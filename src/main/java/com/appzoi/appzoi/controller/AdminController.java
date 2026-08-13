package com.appzoi.appzoi.controller;

import com.appzoi.appzoi.config.AdminUserSeeder;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.MascotaRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.util.Validaciones;
import com.appzoi.appzoi.service.EliminacionCuentaService;
import com.appzoi.appzoi.repository.AdministradorRepositorio;
import com.appzoi.appzoi.model.AdministradorEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Autowired
    private MascotaRepositorio mascotaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EliminacionCuentaService eliminacionCuentaService;

    @Autowired
    private AdministradorRepositorio administradorRepository;


    @GetMapping("/admin")
    public String mostrarAdmin(
            @RequestParam(defaultValue = "todos") String tipo,
            @RequestParam(defaultValue = "") String busqueda,
            @RequestParam(defaultValue = "0") int pagina,
            Model model
    ) {
        String tipoNormalizado = normalizarTipo(tipo);
        String busquedaNormalizada = busqueda == null ? "" : busqueda.trim();
        Page<UsuarioEntity> resultado = usuarioRepository.buscar(
                tipoNormalizado,
                busquedaNormalizada,
                AdminUserSeeder.ADMIN_EMAIL,
                PageRequest.of(Math.max(pagina, 0), 10, Sort.by("nombre", "apellido"))
        );

        long totalUsuarios = usuarioRepository.count();
        long totalVeterinarios = usuarioRepository.countByTipoPerfil("VETERINARIO");
        long totalAdministradores = administradorRepository.count();

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalDuenos", Math.max(totalUsuarios - totalVeterinarios - totalAdministradores, 0));
        model.addAttribute("totalVeterinarios", totalVeterinarios);
        model.addAttribute("totalAdministradores", totalAdministradores);
        model.addAttribute("totalMascotas", mascotaRepository.count());
        model.addAttribute("usuarios", resultado.getContent());
        model.addAttribute("conteoMascotas", contarMascotas(resultado.getContent()));
        model.addAttribute("totalFiltrado", resultado.getTotalElements());
        model.addAttribute("paginaActual", resultado.getNumber());
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("tipoSeleccionado", tipoNormalizado);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("adminEmail", AdminUserSeeder.ADMIN_EMAIL);
        return "admin";
    }

    @PostMapping("/admin/usuarios")
    @Transactional
    public String crearUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes
    ) {
        if (!Validaciones.texto(nombre)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "El nombre debe tener entre 2 y 80 letras.");
            return "redirect:/admin";
        }
        if (!Validaciones.texto(apellido)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "El apellido debe tener entre 2 y 80 letras.");
            return "redirect:/admin";
        }
        if (!Validaciones.email(email)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Ingresa un correo electrónico válido.");
            return "redirect:/admin";
        }
        if (!Validaciones.password(password)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "La contraseña requiere mayúscula, minúscula, número y mínimo 8 caracteres.");
            return "redirect:/admin";
        }
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Las contraseñas no coinciden. Vuelve a escribirlas.");
            redirectAttributes.addFlashAttribute("errorConfirmPassword", "La confirmación debe ser exactamente igual a la contraseña.");
            return "redirect:/admin";
        }
        if (!Validaciones.telefono(telefono)) {
            redirectAttributes.addFlashAttribute("errorAdmin", "El teléfono debe tener entre 7 y 20 caracteres válidos.");
            return "redirect:/admin";
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("errorAdmin", "Ya existe un usuario con ese correo.");
            return "redirect:/admin";
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setTelefono(telefono);
        usuario.setTipoPerfil("ADMIN");

        usuarioRepository.save(usuario);
        AdministradorEntity administrador = new AdministradorEntity();
        administrador.setUsuario(usuario);
        administradorRepository.save(administrador);
        redirectAttributes.addFlashAttribute("successAdmin", "Administrador creado correctamente.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/administradores/{id}/eliminar")
    public String eliminarAdministrador(@PathVariable Integer id, java.security.Principal principal,
            HttpServletRequest request, RedirectAttributes redirectAttributes) throws ServletException {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null || !"ADMIN".equals(usuario.getTipoPerfil())) {
            redirectAttributes.addFlashAttribute("errorAdmin", "El administrador no existe.");
            return "redirect:/admin";
        }
        if (AdminUserSeeder.ADMIN_EMAIL.equalsIgnoreCase(usuario.getEmail())) {
            redirectAttributes.addFlashAttribute("errorAdmin", "El administrador principal está protegido.");
            return "redirect:/admin";
        }
        boolean cuentaPropia = usuario.getEmail().equalsIgnoreCase(principal.getName());
        eliminacionCuentaService.eliminar(usuario);
        if (cuentaPropia) {
            request.logout();
            if (request.getSession(false) != null) request.getSession(false).invalidate();
            return "redirect:/login?cuentaEliminada=true";
        }
        redirectAttributes.addFlashAttribute("successAdmin", "Administrador eliminado correctamente.");
        return "redirect:/admin";
    }

    @GetMapping("/admin/reporte-usuarios.xlsx")
    public void descargarReporteUsuarios(
            @RequestParam(defaultValue = "todos") String tipo,
            @RequestParam(defaultValue = "") String busqueda,
            HttpServletResponse response
    ) throws IOException {
        List<UsuarioEntity> usuarios = usuarioRepository.buscarParaReporte(
                normalizarTipo(tipo),
                busqueda == null ? "" : busqueda.trim(),
                AdminUserSeeder.ADMIN_EMAIL
        );
        Map<Integer, Long> conteos = contarMascotas(usuarios);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte-usuarios-zoi.xlsx\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Usuarios");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);

            Row header = sheet.createRow(0);
            String[] columnas = {"ID", "Tipo", "Nombre", "Apellido", "Correo", "Telefono", "Mascotas", "Fecha nacimiento"};
            for (int i = 0; i < columnas.length; i++) {
                header.createCell(i).setCellValue(columnas[i]);
                header.getCell(i).setCellStyle(headerStyle);
            }

            for (int i = 0; i < usuarios.size(); i++) {
                UsuarioEntity usuario = usuarios.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(valorTexto(usuario.getId()));
                row.createCell(1).setCellValue(tipoUsuario(usuario));
                row.createCell(2).setCellValue(valorTexto(usuario.getNombre()));
                row.createCell(3).setCellValue(valorTexto(usuario.getApellido()));
                row.createCell(4).setCellValue(valorTexto(usuario.getEmail()));
                row.createCell(5).setCellValue(valorTexto(usuario.getTelefono()));
                row.createCell(6).setCellValue(conteos.getOrDefault(usuario.getId(), 0L));
                row.createCell(7).setCellValue(usuario.getFechaNacimiento() == null
                        ? ""
                        : usuario.getFechaNacimiento().format(DATE_FORMATTER));
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private String valorTexto(Object value) {
        return value == null ? "" : value.toString();
    }

    public String tipoUsuario(UsuarioEntity usuario) {
        if ("ADMIN".equals(usuario.getTipoPerfil()) || usuario.getEmail() != null
                && AdminUserSeeder.ADMIN_EMAIL.equalsIgnoreCase(usuario.getEmail())) {
            return "Administrador";
        }
        if ("VETERINARIO".equals(usuario.getTipoPerfil())) {
            return "Veterinario";
        }
        return "Dueno";
    }

    private Map<Integer, Long> contarMascotas(List<UsuarioEntity> usuarios) {
        if (usuarios.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Integer> ids = usuarios.stream().map(UsuarioEntity::getId).toList();
        return mascotaRepository.contarPorDuenos(ids).stream()
                .collect(Collectors.toMap(
                        fila -> (Integer) fila[0],
                        fila -> (Long) fila[1]
                ));
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            return "todos";
        }
        return switch (tipo.toLowerCase()) {
            case "duenos", "veterinarios", "administradores" -> tipo.toLowerCase();
            default -> "todos";
        };
    }
}
