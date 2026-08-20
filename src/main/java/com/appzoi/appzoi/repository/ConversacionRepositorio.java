package com.appzoi.appzoi.repository;

import com.appzoi.appzoi.model.*;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface ConversacionRepositorio extends JpaRepository<ConversacionEntity, Integer> {
    @EntityGraph(attributePaths = {"dueno", "veterinario", "veterinario.usuario", "mascota"})
    Optional<ConversacionEntity> findByDuenoAndVeterinarioAndMascota(UsuarioEntity d, VeterinarioPerfilEntity v, MascotaEntity m);

    @EntityGraph(attributePaths = {"dueno", "veterinario", "veterinario.usuario", "mascota"})
    List<ConversacionEntity> findByDuenoOrderByActualizadaEnDesc(UsuarioEntity d);

    List<ConversacionEntity> findByDueno(UsuarioEntity dueno);

    boolean existsByDuenoAndVeterinario(UsuarioEntity dueno, VeterinarioPerfilEntity veterinario);

    List<ConversacionEntity> findByVeterinario(VeterinarioPerfilEntity veterinario);

    boolean existsByMascotaAndVeterinario_Usuario(MascotaEntity mascota, UsuarioEntity usuario);

    List<ConversacionEntity> findByMascota(MascotaEntity mascota);

    @EntityGraph(attributePaths = {"dueno", "veterinario", "veterinario.usuario", "mascota"})
    List<ConversacionEntity> findByDuenoAndMascotaOrderByActualizadaEnDesc(UsuarioEntity d, MascotaEntity m);

    @EntityGraph(attributePaths = {"dueno", "veterinario", "veterinario.usuario", "mascota"})
    List<ConversacionEntity> findByVeterinarioOrderByPendienteVeterinarioDescActualizadaEnDesc(VeterinarioPerfilEntity v);

    @Override
    @EntityGraph(attributePaths = {"dueno", "veterinario", "veterinario.usuario", "mascota"})
    Optional<ConversacionEntity> findById(Integer id);

    long countByVeterinarioAndPendienteVeterinarioTrue(VeterinarioPerfilEntity v);

    long countByDuenoAndPendienteVeterinarioFalse(UsuarioEntity dueno);
}
