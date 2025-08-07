package com.linsi.gestionusuarios.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BecaRequestDTO {    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El tipo no puede estar vacío")
    private String tipo;

    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un número positivo")
    private BigDecimal monto;

    @NotNull(message = "La fecha de inicio no puede ser nula")
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @NotNull(message = "La duración no puede ser nula")
    @Positive(message = "La duración debe ser un número positivo")
    private Integer duracion;
}
