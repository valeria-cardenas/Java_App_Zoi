package com.appzoi.appzoi.repository;

import com.appzoi.appzoi.model.UsuarioEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByEmail(String email);

    long countByTipoPerfil(String tipoPerfil);

    List<UsuarioEntity> findAllByTipoPerfil(String tipoPerfil);

    @Query("""
            select u from UsuarioEntity u
            where (
                :tipo = 'todos'
                or (:tipo = 'administradores' and (u.tipoPerfil = 'ADMIN' or lower(u.email) = lower(:adminEmail)))
                or (:tipo = 'veterinarios' and u.tipoPerfil = 'VETERINARIO')
                or (:tipo = 'duenos' and lower(u.email) <> lower(:adminEmail)
                    and (u.tipoPerfil is null or u.tipoPerfil not in ('VETERINARIO', 'ADMIN')))
            )
            and (
                :busqueda = ''
                or lower(concat(coalesce(u.nombre, ''), ' ', coalesce(u.apellido, ''), ' ',
                    coalesce(u.email, ''), ' ', coalesce(u.telefono, ''))) like lower(concat('%', :busqueda, '%'))
            )
            """)
    Page<UsuarioEntity> buscar(
            @Param("tipo") String tipo,
            @Param("busqueda") String busqueda,
            @Param("adminEmail") String adminEmail,
            Pageable pageable
    );

    @Query("""
            select u from UsuarioEntity u
            where (
                :tipo = 'todos'
                or (:tipo = 'administradores' and (u.tipoPerfil = 'ADMIN' or lower(u.email) = lower(:adminEmail)))
                or (:tipo = 'veterinarios' and u.tipoPerfil = 'VETERINARIO')
                or (:tipo = 'duenos' and lower(u.email) <> lower(:adminEmail)
                    and (u.tipoPerfil is null or u.tipoPerfil not in ('VETERINARIO', 'ADMIN')))
            )
            and (
                :busqueda = ''
                or lower(concat(coalesce(u.nombre, ''), ' ', coalesce(u.apellido, ''), ' ',
                    coalesce(u.email, ''), ' ', coalesce(u.telefono, ''))) like lower(concat('%', :busqueda, '%'))
            )
            order by u.nombre, u.apellido
            """)
    List<UsuarioEntity> buscarParaReporte(
            @Param("tipo") String tipo,
            @Param("busqueda") String busqueda,
            @Param("adminEmail") String adminEmail
    );
}
