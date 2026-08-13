package com.appzoi.appzoi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Conversacion", uniqueConstraints = @UniqueConstraint(
        columnNames = {"id_dueno", "id_veterinario_perfil", "id_mascota"}))
public class ConversacionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversacion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_dueno", nullable = false)
    private UsuarioEntity dueno;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_veterinario_perfil", nullable = false)
    private VeterinarioPerfilEntity veterinario;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaEntity mascota;
    @Column(nullable = false)
    private boolean pendienteVeterinario;
    @Column(nullable = false)
    private LocalDateTime actualizadaEn;
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'PENDIENTE'")
    private String estado = "PENDIENTE";
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'NORMAL'")
    private String prioridad = "NORMAL";

    @PrePersist @PreUpdate
    void actualizarFecha() { actualizadaEn = LocalDateTime.now(); }
    public Integer getId() { return id; }
    public UsuarioEntity getDueno() { return dueno; }
    public void setDueno(UsuarioEntity dueno) { this.dueno = dueno; }
    public VeterinarioPerfilEntity getVeterinario() { return veterinario; }
    public void setVeterinario(VeterinarioPerfilEntity veterinario) { this.veterinario = veterinario; }
    public MascotaEntity getMascota() { return mascota; }
    public void setMascota(MascotaEntity mascota) { this.mascota = mascota; }
    public boolean isPendienteVeterinario() { return pendienteVeterinario; }
    public void setPendienteVeterinario(boolean pendiente) { this.pendienteVeterinario = pendiente; }
    public LocalDateTime getActualizadaEn() { return actualizadaEn; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
}
