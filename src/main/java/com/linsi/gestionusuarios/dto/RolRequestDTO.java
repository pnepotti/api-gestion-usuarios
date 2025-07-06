package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolRequestDTO {
    @NotBlank(message = "El nombre del rol no puede estar vacío")
    private String nombre;
}
