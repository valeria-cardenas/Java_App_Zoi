package com.appzoi.appzoi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "Mensaje")
public class MensajeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje") private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_conversacion", nullable = false)
    private ConversacionEntity conversacion;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_autor", nullable = false)
    private UsuarioEntity autor;
    @Column(nullable = false, length = 2000) private String contenido;
    @Column(name = "imagen_url", length = 500) private String imagenUrl;
    @Column(nullable = false) private LocalDateTime enviadoEn;
    @Column(nullable = false, columnDefinition = "boolean default false") private boolean leido;
    private LocalDateTime leidoEn;
    @PrePersist void antesDeGuardar() { enviadoEn = LocalDateTime.now(); }
    public Integer getId() { return id; }
    public ConversacionEntity getConversacion() { return conversacion; }
    public void setConversacion(ConversacionEntity conversacion) { this.conversacion = conversacion; }
    public UsuarioEntity getAutor() { return autor; }
    public void setAutor(UsuarioEntity autor) { this.autor = autor; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public LocalDateTime getEnviadoEn() { return enviadoEn; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; this.leidoEn = leido ? LocalDateTime.now() : null; }
    public LocalDateTime getLeidoEn() { return leidoEn; }
}
