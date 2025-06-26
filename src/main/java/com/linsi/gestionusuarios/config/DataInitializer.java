package com.linsi.gestionusuarios.config;

import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.repository.RolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RolRepository rolRepo) {
        return args -> {
            if (rolRepo.findByNombre("ADMINISTRADOR").isEmpty()) {
                rolRepo.save(Rol.builder().nombre("ADMINISTRADOR").build());
            }
            if (rolRepo.findByNombre("DOCENTE").isEmpty()) {
                rolRepo.save(Rol.builder().nombre("DOCENTE").build());
            }
            if (rolRepo.findByNombre("BECARIO").isEmpty()) {
                rolRepo.save(Rol.builder().nombre("BECARIO").build());
            }
        };
    }
}
