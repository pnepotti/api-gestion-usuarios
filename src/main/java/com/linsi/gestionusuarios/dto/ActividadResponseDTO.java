package com.linsi.gestionusuarios.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActividadResponseDTO {
    private Long id;
    private String descripcion;
    private LocalDate fecha;
    private Integer horas;
}
