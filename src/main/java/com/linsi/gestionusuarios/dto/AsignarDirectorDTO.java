package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignarDirectorDTO {
    @NotNull(message = "El ID del director no puede ser nulo")
    private Long directorId;    
}
