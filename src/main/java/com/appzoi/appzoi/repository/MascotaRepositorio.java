package com.appzoi.appzoi.repository;

import com.appzoi.appzoi.model.MascotaEntity;
import com.appzoi.appzoi.model.UsuarioEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MascotaRepositorio extends JpaRepository<MascotaEntity, Integer> {

    Optional<MascotaEntity> findByFotoUrl(String fotoUrl);

    Optional<MascotaEntity> findByCarnetVacunacionUrl(String carnetVacunacionUrl);

    List<MascotaEntity> findByDueno(UsuarioEntity dueno);

    long countByDueno(UsuarioEntity dueno);

    void deleteByDueno(UsuarioEntity dueno);

    Optional<MascotaEntity> findByIdAndDueno(Integer id, UsuarioEntity dueno);

    @Query("select m.dueno.id, count(m) from MascotaEntity m where m.dueno.id in :ids group by m.dueno.id")
    List<Object[]> contarPorDuenos(@Param("ids") List<Integer> ids);
}
