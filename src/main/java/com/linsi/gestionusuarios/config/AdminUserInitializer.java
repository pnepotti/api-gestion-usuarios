package com.linsi.gestionusuarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.RolRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@DependsOn("initRoles")
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.nombre}")
    private String adminNombre;

    @Value("${app.admin.apellido}")
    private String adminApellido;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.dni}")
    private String adminDni;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Rol adminRole = rolRepository.findByNombre("ADMINISTRADOR")
                    .orElseThrow(() -> new RuntimeException("Error Crítico: El rol ADMINISTRADOR no fue encontrado."));

            Usuario adminUser = Usuario.builder().nombre(adminNombre).apellido(adminApellido)
                    .dni(adminDni)
                    .email(adminEmail).password(passwordEncoder.encode(adminPassword)).rol(adminRole).build();

            usuarioRepository.save(adminUser);
            log.info("==> Usuario administrador '{} {}' <{}> creado exitosamente.", adminNombre, adminApellido, adminEmail);
        }
    }
}
