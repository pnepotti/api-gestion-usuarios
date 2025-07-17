package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaRequestDTO {
    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    private String codigo;

    @NotNull(message = "El año es obligatorio.")
    @Positive(message = "El año debe ser un número positivo.")
    private Integer anio;
    
    private String descripcion;
}
