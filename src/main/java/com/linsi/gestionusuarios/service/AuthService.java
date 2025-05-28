package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.JwtResponse;
import com.linsi.gestionusuarios.dto.UsuarioRegistroDTO;
import com.linsi.gestionusuarios.dto.LoginDTO;
import com.linsi.gestionusuarios.model.Rol;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<String> register(UsuarioRegistroDTO dto) {
        if (usuarioRepo.findByEmail(dto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Ya existe un usuario con ese email");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(dto.getNombre());
        nuevo.setApellido(dto.getApellido());
        nuevo.setEmail(dto.getEmail());
        nuevo.setPassword(passwordEncoder.encode(dto.getPassword())); // cifrado de contraseña
        nuevo.setRol(Rol.USER); // asignar rol por defecto

        usuarioRepo.save(nuevo);

        return ResponseEntity.ok("Usuario registrado con éxito");
    }

    public ResponseEntity<JwtResponse> login(LoginDTO dto) {

        System.out.println("➡️ Procesando login para: " + dto.getEmail());
        var usuarioOpt = usuarioRepo.findByEmail(dto.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        var usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtUtil.generateToken(usuario);
        return ResponseEntity.ok(new JwtResponse(token));
    }

}
