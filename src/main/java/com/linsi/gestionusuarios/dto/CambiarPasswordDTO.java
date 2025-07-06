package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordDTO {
    private String passwordActual;
    
    @NotBlank(message = "La nueva contraseña es obligatoria.")
    private String passwordNueva;
}
