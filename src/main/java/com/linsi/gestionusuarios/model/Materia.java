package com.linsi.gestionusuarios.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String codigo;

    private Integer anio;

    private String descripcion;

    @ManyToMany(mappedBy = "materias", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Usuario> integrantes;
}
