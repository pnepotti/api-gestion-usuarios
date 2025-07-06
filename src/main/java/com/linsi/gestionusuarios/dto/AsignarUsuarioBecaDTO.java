package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarUsuarioBecaDTO  {
    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long usuarioId;
}
