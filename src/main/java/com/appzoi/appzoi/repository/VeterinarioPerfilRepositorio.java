package com.appzoi.appzoi.repository;

import com.appzoi.appzoi.model.UsuarioEntity;
import com.appzoi.appzoi.model.VeterinarioPerfilEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface VeterinarioPerfilRepositorio extends JpaRepository<VeterinarioPerfilEntity, Integer> {

    @Override
    @EntityGraph(attributePaths = "usuario")
    List<VeterinarioPerfilEntity> findAll();

    @Override
    @EntityGraph(attributePaths = "usuario")
    Optional<VeterinarioPerfilEntity> findById(Integer id);

    Optional<VeterinarioPerfilEntity> findByUsuario(UsuarioEntity usuario);

    void deleteByUsuario(UsuarioEntity usuario);
}
