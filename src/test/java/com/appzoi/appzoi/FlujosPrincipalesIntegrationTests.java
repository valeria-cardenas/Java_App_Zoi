package com.appzoi.appzoi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.appzoi.appzoi.model.AdministradorEntity;
import com.appzoi.appzoi.model.CalificacionVeterinarioEntity;
import com.appzoi.appzoi.model.ConversacionEntity;
import com.appzoi.appzoi.model.MascotaEntity;
import com.appzoi.appzoi.model.MensajeEntity;
import com.appzoi.appzoi.model.RecordatorioEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.model.VeterinarioPerfilEntity;
import com.appzoi.appzoi.repository.AdministradorRepositorio;
import com.appzoi.appzoi.repository.CalificacionVeterinarioRepositorio;
import com.appzoi.appzoi.repository.ConversacionRepositorio;
import com.appzoi.appzoi.repository.MascotaRepositorio;
import com.appzoi.appzoi.repository.MensajeRepositorio;
import com.appzoi.appzoi.repository.RecordatorioRepositorio;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@Transactional
class FlujosPrincipalesIntegrationTests {

    @Autowired
    MockMvc mvc;
    @Autowired
    UsuarioRepositorio usuarios;
    @Autowired
    MascotaRepositorio mascotas;
    @Autowired
    VeterinarioPerfilRepositorio veterinarios;
    @Autowired
    ConversacionRepositorio conversaciones;
    @Autowired
    MensajeRepositorio mensajes;
    @Autowired
    RecordatorioRepositorio recordatorios;
    @Autowired
    CalificacionVeterinarioRepositorio calificaciones;
    @Autowired
    AdministradorRepositorio administradores;
    @Autowired
    PasswordEncoder passwordEncoder;

    private UsuarioEntity dueno;
    private UsuarioEntity otroDueno;
    private UsuarioEntity usuarioVeterinario;
    private VeterinarioPerfilEntity veterinario;
    private MascotaEntity mascota;

    @BeforeEach
    void prepararEscenario() {
        dueno = guardarUsuario("Ana", "Duena", "ana@gmail.com", "DUENO");
        otroDueno = guardarUsuario("Lina", "Otra", "lina@gmail.com", "DUENO");
        usuarioVeterinario = guardarUsuario("Mario", "Veterinario", "vet@gmail.com", "VETERINARIO");

        veterinario = new VeterinarioPerfilEntity();
        veterinario.setUsuario(usuarioVeterinario);
        veterinario.setEspecialidad("Medicina general");
        veterinario.setNumeroDocumento("12345678");
        veterinario.setExperiencia("Cinco anos de experiencia");
        veterinario.setDescripcion("Atencion integral para mascotas");
        veterinario.setTarjetaProfesional("TP 12345");
        veterinario.setClinica("Clinica Zoi");
        veterinario.setLocalidad("Suba");
        veterinario.setTelefono("3001234567");
        veterinario = veterinarios.save(veterinario);

        mascota = new MascotaEntity();
        mascota.setNombre("Luna");
        mascota.setEspecie("Gatos");
        mascota.setTipoMascota("Gato");
        mascota.setSexo("Hembra");
        mascota.setEdad(3);
        mascota.setEsterilizado("Si");
        mascota.setVacunasVigentes(false);
        mascota.setDueno(dueno);
        mascota = mascotas.save(mascota);
    }

    @Test
    void paginasPublicasYPermisosPorRolFuncionan() throws Exception {
        for (String ruta : new String[]{"/", "/beneficios", "/proposito", "/valores", "/login", "/registro"}) {
            mvc.perform(get(ruta)).andExpect(status().isOk());
        }
        mvc.perform(get("/admin").with(user(dueno.getEmail()).roles("DUENO")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/dueno/home").with(user(usuarioVeterinario.getEmail()).roles("VETERINARIO")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/veterinario/home").with(user(dueno.getEmail()).roles("DUENO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void todasLasVistasPrivadasPrincipalesRenderizanSinError() throws Exception {
        ConversacionEntity chat = nuevaConversacion();
        for (String ruta : new String[]{
                "/dueno/home", "/dueno/perfil", "/dueno/recordatorios",
                "/dueno/recordatorios/nuevo", "/dueno/veterinarios",
                "/dueno/veterinarios/" + veterinario.getId(),
                "/dueno/mascotas/" + mascota.getId(),
                "/mascotas/" + mascota.getId() + "/editar",
                "/chat/" + chat.getId()
        }) {
            mvc.perform(get(ruta).with(user(dueno.getEmail()).roles("DUENO")))
                    .andExpect(status().isOk());
        }
        for (String ruta : new String[]{
                "/veterinario/home", "/veterinario/consultas",
                "/veterinario/mi-perfil", "/veterinario/calificaciones",
                "/veterinario/perfil", "/chat/" + chat.getId()
        }) {
            mvc.perform(get(ruta).with(user(usuarioVeterinario.getEmail()).roles("VETERINARIO")))
                    .andExpect(status().isOk());
        }

        UsuarioEntity admin = guardarUsuario("Admin", "Principal", "admin@zoi.com", "ADMIN");
        AdministradorEntity registro = new AdministradorEntity();
        registro.setUsuario(admin);
        administradores.save(registro);
        mvc.perform(get("/admin").with(user(admin.getEmail()).roles("ADMIN")))
                .andExpect(status().isOk());
        mvc.perform(get("/admin/reporte-usuarios.xlsx").with(user(admin.getEmail()).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    void inicioDeSesionRedirigeSegunRolYRechazaClaveIncorrecta() throws Exception {
        mvc.perform(post("/login").with(csrf())
                        .param("username", dueno.getEmail()).param("password", "ClaveSegura1"))
                .andExpect(redirectedUrl("/dueno/home"));
        mvc.perform(post("/login").with(csrf())
                        .param("username", usuarioVeterinario.getEmail()).param("password", "ClaveSegura1"))
                .andExpect(redirectedUrl("/veterinario/home"));
        mvc.perform(post("/login").with(csrf())
                        .param("username", dueno.getEmail()).param("password", "Incorrecta1"))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void registroIgnoraIdYRolManipulados() throws Exception {
        Integer idExistente = dueno.getId();
        mvc.perform(post("/registro").with(csrf())
                        .param("id", idExistente.toString())
                        .param("tipoPerfil", "ADMIN")
                        .param("nombre", "Nueva")
                        .param("apellido", "Persona")
                        .param("email", "NUEVA@GMAIL.COM")
                        .param("telefono", "3007654321")
                        .param("password", "ClaveSegura1")
                        .param("confirmPassword", "ClaveSegura1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/seleccionar-perfil"));

        UsuarioEntity registrado = usuarios.findByEmail("nueva@gmail.com").orElseThrow();
        assertThat(registrado.getId()).isNotEqualTo(idExistente);
        assertThat(registrado.getTipoPerfil()).isNull();
        assertThat(usuarios.findById(idExistente).orElseThrow().getEmail()).isEqualTo("ana@gmail.com");
    }

    @Test
    void registroRechazaCorreoNoPermitidoYContrasenaDebil() throws Exception {
        mvc.perform(post("/registro").with(csrf())
                        .param("nombre", "Nueva").param("apellido", "Persona")
                        .param("email", "persona@example.com").param("telefono", "3007654321")
                        .param("password", "debil").param("confirmPassword", "debil"))
                .andExpect(redirectedUrl("/registro"));
        assertThat(usuarios.findByEmail("persona@example.com")).isEmpty();
    }

    @Test
    void duenoPuedeCrearEditarYEliminarMascotaPropia() throws Exception {
        mvc.perform(post("/mascotas").with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("nombre", "Toby").param("especie", "Perros")
                        .param("tipoMascota", "Perro").param("sexo", "Macho")
                        .param("edad", "4").param("esterilizado", "No")
                        .param("vacunasNoVigentes", "true"))
                .andExpect(redirectedUrl("/dueno/home"));
        MascotaEntity toby = mascotas.findByDueno(dueno).stream()
                .filter(m -> "Toby".equals(m.getNombre())).findFirst().orElseThrow();

        mvc.perform(post("/mascotas/{id}", toby.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("nombre", "Tobias").param("especie", "Perros")
                        .param("tipoMascota", "Perro").param("sexo", "Macho")
                        .param("edad", "5").param("esterilizado", "No")
                        .param("vacunasNoVigentes", "true"))
                .andExpect(redirectedUrl("/dueno/home"));
        assertThat(mascotas.findById(toby.getId()).orElseThrow().getNombre()).isEqualTo("Tobias");

        mvc.perform(post("/mascotas/{id}/eliminar", toby.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf()))
                .andExpect(redirectedUrl("/dueno/home"));
        assertThat(mascotas.findById(toby.getId())).isEmpty();
    }

    @Test
    void unDuenoNoPuedeModificarMascotaAjena() throws Exception {
        mvc.perform(post("/mascotas/{id}", mascota.getId())
                        .with(user(otroDueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("nombre", "Robada").param("especie", "Gatos")
                        .param("tipoMascota", "Gato").param("sexo", "Hembra")
                        .param("edad", "3").param("esterilizado", "Si")
                        .param("vacunasNoVigentes", "true"))
                .andExpect(redirectedUrl("/dueno/home"));
        assertThat(mascotas.findById(mascota.getId()).orElseThrow().getNombre()).isEqualTo("Luna");
    }

    @Test
    void recordatorioSeCreaCompletaYGeneraRepeticion() throws Exception {
        mvc.perform(post("/dueno/recordatorios")
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("mascotaId", mascota.getId().toString())
                        .param("tipo", "ALIMENTACION").param("titulo", "Dar alimento")
                        .param("fechaHora", LocalDateTime.now().plusHours(2).withSecond(0).withNano(0).toString())
                        .param("repeticion", "DIARIA"))
                .andExpect(redirectedUrl("/dueno/recordatorios"));
        RecordatorioEntity creado = recordatorios.findByMascotaDuenoOrderByCompletadoAscFechaHoraAsc(dueno)
                .stream().filter(r -> "Dar alimento".equals(r.getTitulo())).findFirst().orElseThrow();

        mvc.perform(post("/dueno/recordatorios/{id}/completar", creado.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf()))
                .andExpect(redirectedUrl("/dueno/recordatorios"));
        assertThat(recordatorios.findById(creado.getId()).orElseThrow().isCompletado()).isTrue();
        assertThat(recordatorios.findByMascotaDuenoAndCompletadoFalseOrderByFechaHoraAsc(dueno))
                .anyMatch(r -> "Dar alimento".equals(r.getTitulo()));
    }

    @Test
    void chatRespuestaYCalificacionCumplenReglas() throws Exception {
        mvc.perform(post("/dueno/veterinarios/{id}/chat", veterinario.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("mascotaId", mascota.getId().toString()))
                .andExpect(status().is3xxRedirection());
        ConversacionEntity chat = conversaciones.findByDuenoAndVeterinarioAndMascota(dueno, veterinario, mascota)
                .orElseThrow();

        mvc.perform(post("/chat/{id}/mensajes", chat.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("contenido", "Mi mascota no esta comiendo"))
                .andExpect(redirectedUrl("/chat/" + chat.getId()));
        assertThat(conversaciones.findById(chat.getId()).orElseThrow().isPendienteVeterinario()).isTrue();

        mvc.perform(post("/dueno/veterinarios/{id}/calificar", veterinario.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("estrellas", "5").param("comentario", "Excelente atencion"))
                .andExpect(redirectedUrl("/dueno/veterinarios/" + veterinario.getId()));
        assertThat(calificaciones.findByDuenoAndVeterinario(dueno, veterinario)).isEmpty();

        mvc.perform(post("/chat/{id}/mensajes", chat.getId())
                        .with(user(usuarioVeterinario.getEmail()).roles("VETERINARIO")).with(csrf())
                        .param("contenido", ""))
                .andExpect(redirectedUrl("/chat/" + chat.getId()));
        assertThat(mensajes.findByConversacionOrderByEnviadoEnAsc(chat)).hasSize(1);

        mvc.perform(post("/chat/{id}/mensajes", chat.getId())
                        .with(user(usuarioVeterinario.getEmail()).roles("VETERINARIO")).with(csrf())
                        .param("contenido", "Revisa su hidratacion y agenda una consulta"))
                .andExpect(redirectedUrl("/chat/" + chat.getId()));
        assertThat(conversaciones.findById(chat.getId()).orElseThrow().getEstado()).isEqualTo("RESPONDIDA");

        mvc.perform(post("/dueno/veterinarios/{id}/calificar", veterinario.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("estrellas", "5").param("comentario", "Excelente atencion"))
                .andExpect(redirectedUrl("/dueno/veterinarios/" + veterinario.getId()));
        CalificacionVeterinarioEntity calificacion = calificaciones
                .findByDuenoAndVeterinario(dueno, veterinario).orElseThrow();
        assertThat(calificacion.getEstrellas()).isEqualTo(5);
    }

    @Test
    void tercerosNoPuedenEntrarAlChatNiOrganizarlo() throws Exception {
        ConversacionEntity chat = nuevaConversacion();
        mvc.perform(get("/chat/{id}", chat.getId())
                        .with(user(otroDueno.getEmail()).roles("DUENO")))
                .andExpect(redirectedUrl("/dueno/home"));
        mvc.perform(post("/chat/{id}/organizar", chat.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("estado", "CERRADA").param("prioridad", "URGENTE"))
                .andExpect(redirectedUrl("/chat/" + chat.getId()));
        assertThat(conversaciones.findById(chat.getId()).orElseThrow().getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    void administradorCreadoQuedaEnAmbasTablas() throws Exception {
        UsuarioEntity admin = guardarUsuario("Admin", "Principal", "admin@zoi.com", "ADMIN");
        AdministradorEntity registro = new AdministradorEntity();
        registro.setUsuario(admin);
        administradores.save(registro);

        mvc.perform(post("/admin/usuarios").with(user(admin.getEmail()).roles("ADMIN")).with(csrf())
                        .param("nombre", "Segundo").param("apellido", "Admin")
                        .param("email", "segundo@zoi.com").param("telefono", "3001234567")
                        .param("password", "AdminSeguro1").param("confirmPassword", "AdminSeguro1"))
                .andExpect(redirectedUrl("/admin"));
        UsuarioEntity creado = usuarios.findByEmail("segundo@zoi.com").orElseThrow();
        assertThat(creado.getTipoPerfil()).isEqualTo("ADMIN");
        assertThat(administradores.existsByUsuario(creado)).isTrue();
    }

    @Test
    void contrasenaIncorrectaNoEliminaCuenta() throws Exception {
        mvc.perform(post("/perfil/eliminar").with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("password", "Incorrecta1"))
                .andExpect(redirectedUrl("/dueno/perfil#eliminar"));
        assertThat(usuarios.findByEmail(dueno.getEmail())).isPresent();
    }

    @Test
    void eliminarMascotaLimpiaChatsMensajesYRecordatorios() throws Exception {
        ConversacionEntity chat = nuevaConversacion();
        MensajeEntity mensaje = new MensajeEntity();
        mensaje.setConversacion(chat);
        mensaje.setAutor(dueno);
        mensaje.setContenido("Consulta asociada");
        mensajes.save(mensaje);
        RecordatorioEntity recordatorio = new RecordatorioEntity();
        recordatorio.setMascota(mascota);
        recordatorio.setTipo("CITA");
        recordatorio.setTitulo("Consulta anual");
        recordatorio.setFechaHora(LocalDateTime.now().plusDays(2));
        recordatorio.setRepeticion("NINGUNA");
        recordatorios.save(recordatorio);

        mvc.perform(post("/mascotas/{id}/eliminar", mascota.getId())
                        .with(user(dueno.getEmail()).roles("DUENO")).with(csrf()))
                .andExpect(redirectedUrl("/dueno/home"));

        assertThat(mascotas.findById(mascota.getId())).isEmpty();
        assertThat(conversaciones.findById(chat.getId())).isEmpty();
        assertThat(mensajes.findById(mensaje.getId())).isEmpty();
        assertThat(recordatorios.findById(recordatorio.getId())).isEmpty();
    }

    @Test
    void eliminarCuentaConClaveCorrectaLimpiaTodasSusRelaciones() throws Exception {
        ConversacionEntity chat = nuevaConversacion();
        MensajeEntity mensaje = new MensajeEntity();
        mensaje.setConversacion(chat);
        mensaje.setAutor(dueno);
        mensaje.setContenido("Mensaje antes de eliminar");
        mensajes.save(mensaje);

        mvc.perform(post("/perfil/eliminar").with(user(dueno.getEmail()).roles("DUENO")).with(csrf())
                        .param("password", "ClaveSegura1"))
                .andExpect(redirectedUrl("/login?cuentaEliminada=true"));

        assertThat(usuarios.findByEmail(dueno.getEmail())).isEmpty();
        assertThat(mascotas.findById(mascota.getId())).isEmpty();
        assertThat(conversaciones.findById(chat.getId())).isEmpty();
        assertThat(mensajes.findById(mensaje.getId())).isEmpty();
        assertThat(usuarios.findByEmail(usuarioVeterinario.getEmail())).isPresent();
    }

    private UsuarioEntity guardarUsuario(String nombre, String apellido, String email, String rol) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono("3001234567");
        usuario.setPassword(passwordEncoder.encode("ClaveSegura1"));
        usuario.setTipoPerfil(rol);
        return usuarios.save(usuario);
    }

    private ConversacionEntity nuevaConversacion() {
        ConversacionEntity chat = new ConversacionEntity();
        chat.setDueno(dueno);
        chat.setVeterinario(veterinario);
        chat.setMascota(mascota);
        chat.setEstado("PENDIENTE");
        chat.setPrioridad("NORMAL");
        return conversaciones.save(chat);
    }
}
