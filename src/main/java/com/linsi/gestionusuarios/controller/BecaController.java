package com.linsi.gestionusuarios.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.linsi.gestionusuarios.repository.BecaRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.MensajeResponseDTO;
import com.linsi.gestionusuarios.dto.AsignarUsuarioBecaDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.model.Beca;
import com.linsi.gestionusuarios.model.Usuario;

@RestController
@RequestMapping("/api/becas")
@Tag(name = "Gestión de Becas", description = "API para la creación y gestión de las becas")
public class BecaController {

    @Autowired
    private BecaRepository becaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BecaResponseDTO convertToDto(Beca beca) {
        BecaResponseDTO dto = new BecaResponseDTO();
        dto.setId(beca.getId());
        dto.setNombre(beca.getNombre());
        dto.setTipo(beca.getTipo());
        dto.setMonto(beca.getMonto());
        dto.setFechaInicio(beca.getFechaInicio());
        dto.setFechaFin(beca.getFechaFin());
        dto.setDuracion(beca.getDuracion());
        if (beca.getUsuario() != null) {
            dto.setUsuario(convertUsuarioToDto(beca.getUsuario()));
        }
        return dto;
    }

    private UsuarioResponseDTO convertUsuarioToDto(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public List<BecaResponseDTO> listarBecas() {
        return becaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{becaId}")
    public ResponseEntity<BecaResponseDTO> obtenerBeca(@PathVariable Long becaId) {
        return becaRepository.findById(becaId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public BecaResponseDTO crearBeca(@Valid @RequestBody BecaRequestDTO becaDto) {
        Beca nuevaBeca = new Beca();
        nuevaBeca.setNombre(becaDto.getNombre());
        nuevaBeca.setTipo(becaDto.getTipo());
        nuevaBeca.setMonto(becaDto.getMonto());
        nuevaBeca.setFechaInicio(becaDto.getFechaInicio());
        nuevaBeca.setFechaFin(becaDto.getFechaFin());
        nuevaBeca.setDuracion(becaDto.getDuracion());

        Beca becaGuardada = becaRepository.save(nuevaBeca);
        return convertToDto(becaGuardada);
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{becaId}")
    public ResponseEntity<BecaResponseDTO> actualizarBeca(@PathVariable Long becaId, @Valid @RequestBody BecaRequestDTO becaDto) {
        return becaRepository.findById(becaId).map(becaExistente -> {
            becaExistente.setNombre(becaDto.getNombre());
            becaExistente.setTipo(becaDto.getTipo());
            becaExistente.setMonto(becaDto.getMonto());
            becaExistente.setFechaInicio(becaDto.getFechaInicio());
            becaExistente.setFechaFin(becaDto.getFechaFin());
            becaExistente.setDuracion(becaDto.getDuracion());
            Beca becaActualizada = becaRepository.save(becaExistente);
            return ResponseEntity.ok(convertToDto(becaActualizada));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{becaId}")
    public ResponseEntity<Void> eliminarBeca(@PathVariable Long becaId) {
        if (!becaRepository.existsById(becaId)) {
            return ResponseEntity.notFound().build();
        }
        becaRepository.deleteById(becaId);
        return ResponseEntity.noContent().build();    }

    //USUARIOS
    
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{becaId}/usuario")
    public ResponseEntity<?> asignarUsuarioABeca(
            @PathVariable Long becaId,
            @Valid @RequestBody AsignarUsuarioBecaDTO dto) {
        
        //Buscar la beca
        Optional<Beca> becaOpt = becaRepository.findById(becaId);
        if (becaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Beca no encontrada."));
        }
        Beca beca = becaOpt.get();

        //Buscar el usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Usuario no encontrado."));
        }
        Usuario usuario = usuarioOpt.get();

        //Validar la lógica de negocio
        if (beca.getUsuario() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new MensajeResponseDTO("La beca ya está asignada a otro usuario. Quite el usuario actual primero."));
        }

        //Asignar, guardar y devolver la respuesta
        beca.setUsuario(usuario);
        Beca becaGuardada = becaRepository.save(beca);
        return ResponseEntity.ok(convertToDto(becaGuardada));
    }    

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{becaId}/usuario")
    public ResponseEntity<?> quitarUsuarioDeBeca(@PathVariable Long becaId) {
        Optional<Beca> becaOpt = becaRepository.findById(becaId);
        if (becaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Beca no encontrada."));
        }

        Beca beca = becaOpt.get();
        if (beca.getUsuario() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("La beca no tiene ningún usuario asignado."));
        }

        beca.setUsuario(null);
        becaRepository.save(beca);
        return ResponseEntity.noContent().build();
    }

}