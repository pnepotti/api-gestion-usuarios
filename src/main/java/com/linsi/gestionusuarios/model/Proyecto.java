package com.linsi.gestionusuarios.model;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descripcion;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "director_id")
    private Usuario director;

    @ManyToMany
    @JoinTable(
        name = "proyecto_usuario",
        joinColumns = @JoinColumn(name = "proyecto_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> integrantes;

    @OneToMany(mappedBy = "proyecto", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Actividad> actividades;
}
