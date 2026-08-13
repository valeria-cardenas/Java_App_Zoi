package com.appzoi.appzoi.service;

import com.appzoi.appzoi.model.*;
import com.appzoi.appzoi.repository.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminacionCuentaService {
    private final UsuarioRepositorio usuarios;
    private final MascotaRepositorio mascotas;
    private final VeterinarioPerfilRepositorio veterinarios;
    private final ConversacionRepositorio conversaciones;
    private final MensajeRepositorio mensajes;
    private final RecordatorioRepositorio recordatorios;
    private final AdministradorRepositorio administradores;
    private final CalificacionVeterinarioRepositorio calificaciones;

    public EliminacionCuentaService(UsuarioRepositorio usuarios, MascotaRepositorio mascotas,
            VeterinarioPerfilRepositorio veterinarios, ConversacionRepositorio conversaciones,
            MensajeRepositorio mensajes, RecordatorioRepositorio recordatorios,
            AdministradorRepositorio administradores, CalificacionVeterinarioRepositorio calificaciones) {
        this.usuarios = usuarios; this.mascotas = mascotas; this.veterinarios = veterinarios;
        this.conversaciones = conversaciones; this.mensajes = mensajes; this.recordatorios = recordatorios;
        this.administradores = administradores;
        this.calificaciones = calificaciones;
    }

    @Transactional
    public void eliminar(UsuarioEntity usuario) {
        Map<Integer, ConversacionEntity> asociadas = new LinkedHashMap<>();
        conversaciones.findByDueno(usuario).forEach(c -> asociadas.put(c.getId(), c));
        VeterinarioPerfilEntity perfil = veterinarios.findByUsuario(usuario).orElse(null);
        calificaciones.deleteByDueno(usuario);
        if (perfil != null) calificaciones.deleteByVeterinario(perfil);
        if (perfil != null) conversaciones.findByVeterinario(perfil).forEach(c -> asociadas.put(c.getId(), c));

        List<ConversacionEntity> lista = List.copyOf(asociadas.values());
        if (!lista.isEmpty()) {
            mensajes.deleteByConversacionIn(lista);
            conversaciones.deleteAll(lista);
        }
        mensajes.deleteByAutor(usuario);
        recordatorios.deleteByMascotaDueno(usuario);
        mascotas.deleteByDueno(usuario);
        if (perfil != null) veterinarios.delete(perfil);
        administradores.deleteByUsuario(usuario);
        usuarios.delete(usuario);
    }
}
