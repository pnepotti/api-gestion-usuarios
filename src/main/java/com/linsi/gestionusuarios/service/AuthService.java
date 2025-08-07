package com.linsi.gestionusuarios.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.linsi.gestionusuarios.dto.JwtResponse;
import com.linsi.gestionusuarios.dto.LoginDTO;
import com.linsi.gestionusuarios.dto.UsuarioRegistroDTO;
import com.linsi.gestionusuarios.exception.DniAlreadyExistsException;
import com.linsi.gestionusuarios.exception.EmailAlreadyExistsException;
import com.linsi.gestionusuarios.exception.LegajoAlreadyExistsException;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import com.linsi.gestionusuarios.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void register(UsuarioRegistroDTO dto) {
        
        if (usuarioRepo.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con ese email");
        }

        if (usuarioRepo.existsByDni(dto.getDni())) {
            throw new DniAlreadyExistsException("Ya existe un usuario con ese DNI");
        }

        if (dto.getLegajo() != null && usuarioRepo.existsByLegajo(dto.getLegajo())) {
            throw new LegajoAlreadyExistsException("Ya existe un usuario con ese legajo");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono() != null ? dto.getTelefono() : null)
                .direccion(dto.getDireccion() != null ? dto.getDireccion() : null)
                .legajo(dto.getLegajo() != null ? dto.getLegajo() : null)
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        usuarioRepo.save(nuevoUsuario);
    }

    public JwtResponse login(LoginDTO dto) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        Usuario usuario = (Usuario) authentication.getPrincipal();

        String token = jwtUtil.generateToken(usuario);
        return new JwtResponse(token);
    }

}
