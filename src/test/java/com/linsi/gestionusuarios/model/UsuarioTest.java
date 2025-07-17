package com.linsi.gestionusuarios.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;

class UsuarioTest {

    @Test
    void testCrearUsuarioYVerificarPropiedades() {
        // 1. Arrange (Preparar)
        Rol rolAdmin = new Rol();
        rolAdmin.setId(1L);
        rolAdmin.setNombre("ADMINISTRADOR");

        Set<Rol> roles = new HashSet<>();
        roles.add(rolAdmin);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("John");
        usuario.setApellido("Doe");
        usuario.setDni("12345678");
        usuario.setLegajo("F-1234");
        usuario.setEmail("john.doe@example.com");
        usuario.setPassword("password123");
        usuario.setRol(rolAdmin);

        // 2. Act & Assert (Actuar y Afirmar)
        assertThat(usuario.getId()).isEqualTo(1L);
        assertThat(usuario.getNombre()).isEqualTo("John");
        assertThat(usuario.getApellido()).isEqualTo("Doe");
        assertThat(usuario.getDni()).isEqualTo("12345678");
        assertThat(usuario.getLegajo()).isEqualTo("F-1234");
        assertThat(usuario.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(usuario.getPassword()).isEqualTo("password123");
        assertThat(usuario.isEnabled()).isTrue();
        
        // Verificar la relación con Rol
        assertThat(usuario.getRol()).isEqualTo(rolAdmin);
        // Verificamos que getAuthorities funcione como se espera
        assertThat(usuario.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMINISTRADOR");
    }

    @Test
    void testConstructorSinArgumentos() {
        // Arrange & Act
        Usuario usuario = new Usuario();

        // Assert
        assertThat(usuario.getId()).isNull();
        assertThat(usuario.getNombre()).isNull();
        assertThat(usuario.getEmail()).isNull();
    }
    
    // métodos equals() y hashCode(), etc.
}

