package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @Email(message = "El email debe ser válido.")
    @NotBlank(message = "El email es obligatorio.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    private String password;
}
