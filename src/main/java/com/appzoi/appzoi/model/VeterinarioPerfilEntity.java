package com.appzoi.appzoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "VeterinarioPerfil")
public class VeterinarioPerfilEntity {
    @Transient private Double promedioCalificacion;
    @Transient private Long totalCalificaciones;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veterinario_perfil")
    private Integer id;

    @Column(nullable = false, length = 120)
    private String especialidad;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Column(length = 120)
    private String experiencia;

    @Column(length = 800)
    private String descripcion;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(name = "titulo_url", length = 500)
    private String tituloUrl;

    @Column(name = "tarjeta_profesional", length = 80)
    private String tarjetaProfesional;

    @Column(length = 120)
    private String clinica;

    @Column(length = 20)
    private String telefono;
    @Column(length = 60)
    private String localidad;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dueno", nullable = false, unique = true)
    private UsuarioEntity usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getTituloUrl() {
        return tituloUrl;
    }

    public void setTituloUrl(String tituloUrl) {
        this.tituloUrl = tituloUrl;
    }

    public String getTarjetaProfesional() {
        return tarjetaProfesional;
    }

    public void setTarjetaProfesional(String tarjetaProfesional) {
        this.tarjetaProfesional = tarjetaProfesional;
    }

    public String getClinica() {
        return clinica;
    }

    public void setClinica(String clinica) {
        this.clinica = clinica;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }
    public Double getPromedioCalificacion(){return promedioCalificacion;} public void setPromedioCalificacion(Double v){promedioCalificacion=v;}
    public Long getTotalCalificaciones(){return totalCalificaciones;} public void setTotalCalificaciones(Long v){totalCalificaciones=v;}
}
