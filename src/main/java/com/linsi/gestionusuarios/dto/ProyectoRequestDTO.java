package com.linsi.gestionusuarios.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoRequestDTO {
    @NotBlank(message = "El título no puede estar vacío")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;
    
    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
}
