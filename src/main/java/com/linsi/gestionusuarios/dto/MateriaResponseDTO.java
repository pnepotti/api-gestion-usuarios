package com.linsi.gestionusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaResponseDTO {
    private Long id;
    private String nombre;
    private String codigo;
    private Integer anio;
    private String descripcion;
}
