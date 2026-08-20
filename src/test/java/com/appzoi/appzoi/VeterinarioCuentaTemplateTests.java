package com.appzoi.appzoi;

import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.repository.UsuarioRepositorio;
import com.appzoi.appzoi.repository.VeterinarioPerfilRepositorio;
import com.appzoi.appzoi.model.VeterinarioPerfilEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VeterinarioCuentaTemplateTests {
    @Autowired
    MockMvc mvc;
    @Autowired
    UsuarioRepositorio usuarios;
    @Autowired
    VeterinarioPerfilRepositorio perfiles;

    @BeforeEach
    void crearVeterinario() {
        UsuarioEntity usuario = usuarios.findByEmail("vet@prueba.com").orElse(null);
        if (usuario == null) {
            usuario = new UsuarioEntity();
            usuario.setNombre("Veterinario");
            usuario.setApellido("Prueba");
            usuario.setEmail("vet@prueba.com");
            usuario.setPassword("codificada");
            usuario.setTipoPerfil("VETERINARIO");
            usuarios.save(usuario);
        }
        if (perfiles.findByUsuario(usuario).isEmpty()) {
            VeterinarioPerfilEntity perfil = new VeterinarioPerfilEntity();
            perfil.setUsuario(usuario);
            perfil.setEspecialidad("Medicina general");
            perfiles.save(perfil);
        }
    }

    @Test
    @WithMockUser(username = "vet@prueba.com", roles = "VETERINARIO")
    void muestraPerfilVeterinario() throws Exception {
        mvc.perform(get("/veterinario/mi-perfil"))
                .andExpect(status().isOk())
                .andExpect(view().name("veterinario_cuenta"));
    }
}
