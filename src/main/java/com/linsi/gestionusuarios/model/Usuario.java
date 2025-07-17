package com.linsi.gestionusuarios.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
@ToString(exclude = {"rol", "materias", "becas", "proyectos", "proyectosDirigidos"})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String dni;

    @Column(unique = true, nullable = true)
    private String legajo;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol; // Puede ser null al crear el usuario

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_materia",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    private Set<Materia> materias = new HashSet<>();

    // Si un usuario es eliminado, sus becas también deberían serlo.
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Beca> becas = new HashSet<>();

    @ManyToMany(mappedBy = "integrantes", fetch = FetchType.LAZY)
    private Set<Proyecto> proyectos = new HashSet<>();

    @OneToMany(mappedBy = "director", fetch = FetchType.LAZY)
    private Set<Proyecto> proyectosDirigidos = new HashSet<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
