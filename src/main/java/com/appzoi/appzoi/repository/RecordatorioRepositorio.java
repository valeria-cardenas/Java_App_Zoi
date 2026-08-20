package com.appzoi.appzoi.repository;

import com.appzoi.appzoi.model.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

public interface RecordatorioRepositorio extends JpaRepository<RecordatorioEntity, Integer> {
    @Override
    @EntityGraph(attributePaths = {"mascota", "mascota.dueno"})
    Optional<RecordatorioEntity> findById(Integer id);

    @EntityGraph(attributePaths = {"mascota", "mascota.dueno"})
    List<RecordatorioEntity> findByMascotaDuenoOrderByCompletadoAscFechaHoraAsc(UsuarioEntity dueno);

    @EntityGraph(attributePaths = {"mascota", "mascota.dueno"})
    List<RecordatorioEntity> findByMascotaDuenoAndCompletadoFalseOrderByFechaHoraAsc(UsuarioEntity dueno);

    @EntityGraph(attributePaths = {"mascota", "mascota.dueno"})
    List<RecordatorioEntity> findByMascotaOrderByCompletadoAscFechaHoraAsc(MascotaEntity mascota);

    long countByMascotaDuenoAndCompletadoFalseAndFechaHoraBefore(UsuarioEntity dueno, LocalDateTime fecha);

    @Transactional
    void deleteByMascota(MascotaEntity mascota);

    @Transactional
    void deleteByMascotaDueno(UsuarioEntity dueno);
}
