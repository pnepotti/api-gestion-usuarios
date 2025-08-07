package com.linsi.gestionusuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Gestión de Usuarios - LINSI")
                        .version("1.0")
                        .description("Documentación de la API para gestionar usuarios con roles y autenticación JWT."));
    }
}
