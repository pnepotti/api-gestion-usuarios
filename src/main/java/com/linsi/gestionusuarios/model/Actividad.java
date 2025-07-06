package com.linsi.gestionusuarios.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    private LocalDate fecha;
    
    private Integer horas;

    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    private Proyecto proyecto;

}
