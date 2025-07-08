package com.linsi.gestionusuarios.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String apellido;

    @Column(unique = true)
    private String dni;

    @Column(unique = true)
    private String legajo;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol; // Puede ser null al crear el usuario

    @ManyToMany
    @JoinTable(
        name = "usuario_materia",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    private Set<Materia> materias;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Beca> becas;

    @ManyToMany(mappedBy = "integrantes")
    private Set<Proyecto> proyectos;

    @OneToMany(mappedBy = "director", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Proyecto> proyectosDirigidos;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.rol != null && this.rol.getNombre() != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombre()));
        }
        // Devuelve una lista vacía si no hay rol, para evitar NullPointerException.
        return List.of();
    }

    @Override
    public String getUsername() {
        // El "username" para Spring Security será el email.
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
