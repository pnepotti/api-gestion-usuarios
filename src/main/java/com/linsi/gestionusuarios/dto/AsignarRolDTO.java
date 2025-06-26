package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignarRolDTO {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long rolId;
}
