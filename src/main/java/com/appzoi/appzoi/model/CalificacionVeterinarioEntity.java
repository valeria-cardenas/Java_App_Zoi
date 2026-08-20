package com.appzoi.appzoi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificacion_veterinario", uniqueConstraints = @UniqueConstraint(columnNames = {"id_dueno", "id_veterinario_perfil"}))
public class CalificacionVeterinarioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion") private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_dueno", nullable = false) private UsuarioEntity dueno;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_veterinario_perfil", nullable = false) private VeterinarioPerfilEntity veterinario;
    @Column(nullable = false) private Integer estrellas;
    @Column(nullable = false, length = 800) private String comentario;
    @Column(nullable = false) private LocalDateTime actualizadaEn;
    @PrePersist @PreUpdate void fecha() { actualizadaEn = LocalDateTime.now(); }
    public Integer getId(){return id;} public UsuarioEntity getDueno(){return dueno;} public void setDueno(UsuarioEntity v){dueno=v;}
    public VeterinarioPerfilEntity getVeterinario(){return veterinario;} public void setVeterinario(VeterinarioPerfilEntity v){veterinario=v;}
    public Integer getEstrellas(){return estrellas;} public void setEstrellas(Integer v){estrellas=v;}
    public String getComentario(){return comentario;} public void setComentario(String v){comentario=v;}
    public LocalDateTime getActualizadaEn(){return actualizadaEn;}
}
