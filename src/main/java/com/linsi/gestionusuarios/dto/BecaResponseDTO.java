package com.linsi.gestionusuarios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BecaResponseDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private BigDecimal monto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer duracion;
    private UsuarioResponseDTO usuario;
}
