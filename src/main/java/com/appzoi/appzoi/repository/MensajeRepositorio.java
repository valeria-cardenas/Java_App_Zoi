package com.appzoi.appzoi.repository;
import com.appzoi.appzoi.model.*;
import java.util.List;
import com.appzoi.appzoi.model.UsuarioEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
public interface MensajeRepositorio extends JpaRepository<MensajeEntity, Integer> {
    @EntityGraph(attributePaths = "autor")
    List<MensajeEntity> findByConversacionOrderByEnviadoEnAsc(ConversacionEntity conversacion);
    @Transactional void deleteByConversacionIn(List<ConversacionEntity> conversaciones);
    @Transactional void deleteByAutor(UsuarioEntity autor);
    long countByConversacionAndAutorNotAndLeidoFalse(ConversacionEntity conversacion, UsuarioEntity autor);
    boolean existsByConversacion_DuenoAndConversacion_VeterinarioAndAutor(
            UsuarioEntity dueno, VeterinarioPerfilEntity veterinario, UsuarioEntity autor);
    @Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("update MensajeEntity m set m.leido=true, m.leidoEn=CURRENT_TIMESTAMP where m.conversacion=:chat and m.autor<>:lector and m.leido=false")
    int marcarLeidos(ConversacionEntity chat, UsuarioEntity lector);
    long countByConversacion(ConversacionEntity conversacion);
    long countByConversacionAndAutorAndLeidoFalse(ConversacionEntity conversacion, UsuarioEntity autor);
}
