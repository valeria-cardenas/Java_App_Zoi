package com.appzoi.appzoi.repository;

import com.appzoi.appzoi.model.AdministradorEntity;
import com.appzoi.appzoi.model.UsuarioEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AdministradorRepositorio extends JpaRepository<AdministradorEntity, Integer> {
    Optional<AdministradorEntity> findByUsuario(UsuarioEntity usuario);
    boolean existsByUsuario(UsuarioEntity usuario);
    @Transactional void deleteByUsuario(UsuarioEntity usuario);
}
