package com.appzoi.appzoi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recordatorio")
public class RecordatorioEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recordatorio") private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_mascota", nullable = false)
    private MascotaEntity mascota;
    @Column(nullable = false, length = 30) private String tipo;
    @Column(nullable = false, length = 120) private String titulo;
    @Column(length = 600) private String descripcion;
    @Column(name = "fecha_hora", nullable = false) private LocalDateTime fechaHora;
    @Column(nullable = false, length = 20) private String repeticion = "NINGUNA";
    @Column(nullable = false) private boolean completado;

    public Integer getId() { return id; }
    public MascotaEntity getMascota() { return mascota; }
    public void setMascota(MascotaEntity mascota) { this.mascota = mascota; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public String getRepeticion() { return repeticion; }
    public void setRepeticion(String repeticion) { this.repeticion = repeticion; }
    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
}
