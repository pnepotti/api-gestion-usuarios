package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarIntegranteDTO {
    @NotNull(message = "El ID del usuario no puede ser nulo")
    private Long usuarioId;
}
