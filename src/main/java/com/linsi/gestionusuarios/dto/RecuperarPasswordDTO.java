package com.linsi.gestionusuarios.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RecuperarPasswordDTO {

    @Email
    private String email;

}
