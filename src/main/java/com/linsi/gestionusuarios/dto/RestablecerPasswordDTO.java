package com.linsi.gestionusuarios.dto;

import lombok.Data;

@Data
public class RestablecerPasswordDTO {
    private String token;
    private String nuevaPassword;
}
