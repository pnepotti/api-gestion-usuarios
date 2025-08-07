package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestablecerPasswordDTO {
    @NotBlank(message = "El token no puede estar vacío")
    private String token;
    
    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String nuevaPassword;
}
