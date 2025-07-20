package com.linsi.gestionusuarios.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"integrantes"})
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String codigo;

    @Column(nullable = false)
    private Integer anio;

    private String descripcion;

    @ManyToMany(mappedBy = "materias", fetch = FetchType.LAZY)
    private Set<Usuario> integrantes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materia materia = (Materia) o;
        // La igualdad se basa en la clave de negocio 'codigo', que es única.
        return Objects.equals(codigo, materia.codigo);
    }

    @Override
    public int hashCode() {
        // El hash se basa en la clave de negocio 'codigo'.
        return Objects.hash(codigo);
    }
}
