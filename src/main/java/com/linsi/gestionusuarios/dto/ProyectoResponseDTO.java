package com.linsi.gestionusuarios.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoResponseDTO {    
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private UsuarioResponseDTO director;
    private List<UsuarioResponseDTO> integrantes;
    private AreaResponseDTO area;
}
