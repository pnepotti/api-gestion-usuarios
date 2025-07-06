package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.JwtResponse;
import com.linsi.gestionusuarios.dto.UsuarioRegistroDTO;
import com.linsi.gestionusuarios.exception.EmailAlreadyExistsException;
import com.linsi.gestionusuarios.dto.LoginDTO;
import org.springframework.security.core.Authentication;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.linsi.gestionusuarios.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public void register(UsuarioRegistroDTO dto) {
        if (usuarioRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con ese email");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(dto.getNombre());
        nuevo.setApellido(dto.getApellido());
        nuevo.setEmail(dto.getEmail());
        nuevo.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        usuarioRepo.save(nuevo);
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
