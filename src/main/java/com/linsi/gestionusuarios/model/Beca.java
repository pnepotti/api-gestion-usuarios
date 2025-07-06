package com.linsi.gestionusuarios.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Beca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;

    private String tipo;

    private BigDecimal monto;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Integer duracion; // en meses

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

}
