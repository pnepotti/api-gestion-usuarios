package com.linsi.gestionusuarios.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.linsi.gestionusuarios.dto.AsignarIntegranteMateriaDTO;
import com.linsi.gestionusuarios.dto.MateriaRequestDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.MensajeResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.model.Materia;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.MateriaRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/materias")
@Tag(name = "Gestión de Materias", description = "API para la creación y gestión de las materias")
public class MateriaController {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioResponseDTO convertUsuarioToDto(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }

    private MateriaResponseDTO convertToDto(Materia materia) {
        MateriaResponseDTO dto = new MateriaResponseDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setCodigo(materia.getCodigo());
        dto.setAnio(materia.getAnio());
        dto.setDescripcion(materia.getDescripcion());
        return dto;
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping
    public List<MateriaResponseDTO> listarMaterias() {
        return materiaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{materiaId}")
     public ResponseEntity<MateriaResponseDTO> obtenerMateria(@PathVariable Long materiaId) {
        return materiaRepository.findById(materiaId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crearMateria(@Valid @RequestBody MateriaRequestDTO materiaDto) {
        Materia nuevaMateria = new Materia();
        nuevaMateria.setNombre(materiaDto.getNombre());
        nuevaMateria.setCodigo(materiaDto.getCodigo());
        nuevaMateria.setAnio(materiaDto.getAnio());
        nuevaMateria.setDescripcion(materiaDto.getDescripcion());
        
        Materia materiaGuardada = materiaRepository.save(nuevaMateria);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(materiaGuardada));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PutMapping("/{materiaId}")
    public ResponseEntity<MateriaResponseDTO> actualizarMateria(@PathVariable Long materiaId, @Valid @RequestBody MateriaRequestDTO materiaDto) {
        return materiaRepository.findById(materiaId).map(materiaExistente -> {
            materiaExistente.setNombre(materiaDto.getNombre());
            materiaExistente.setCodigo(materiaDto.getCodigo());
            materiaExistente.setAnio(materiaDto.getAnio());
            materiaExistente.setDescripcion(materiaDto.getDescripcion());
            Materia materiaActualizada = materiaRepository.save(materiaExistente);
            return ResponseEntity.ok(convertToDto(materiaActualizada));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @DeleteMapping("/{materiaId}")
    public ResponseEntity<?> eliminarMateria(@PathVariable Long materiaId) {
        Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
        if (materiaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Materia materia = materiaOpt.get();
        if (!materia.getIntegrantes().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MensajeResponseDTO("No se puede eliminar la materia porque tiene integrantes asignados."));
        }
        materiaRepository.deleteById(materiaId);
        return ResponseEntity.noContent().build();
    }

    //USUARIOS

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @PostMapping("/{materiaId}/integrantes")
    public ResponseEntity<?> asignarUsuarioAMateria(
            @PathVariable Long materiaId,
            @Valid @RequestBody AsignarIntegranteMateriaDTO dto) {
        
        Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
        if (materiaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Materia no encontrada."));
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dto.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Usuario no encontrado."));
        }

        Materia materia = materiaOpt.get();
        Usuario usuario = usuarioOpt.get();

        if (materia.getIntegrantes().contains(usuario)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MensajeResponseDTO("El usuario ya es integrante de la materia."));
        }

        materia.getIntegrantes().add(usuario);
        materiaRepository.save(materia);
        return ResponseEntity.ok(convertToDto(materia));
    }    

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @DeleteMapping("/{materiaId}/integrantes/{usuarioId}")
    public ResponseEntity<?> quitarUsuarioDeMateria(
            @PathVariable Long materiaId,
            @PathVariable Long usuarioId) {
               
        Optional<Materia> materiaOpt = materiaRepository.findById(materiaId);
        if (materiaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("Materia no encontrada."));
        }
        Materia materia = materiaOpt.get();
        boolean removed = materia.getIntegrantes().removeIf(integrante -> integrante.getId().equals(usuarioId));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDTO("El usuario no es integrante de esta materia."));
        }
        materiaRepository.save(materia);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('DOCENTE')")
    @GetMapping("/{materiaId}/integrantes")
    public ResponseEntity<List<UsuarioResponseDTO>> listarIntegrantesDeMateria(@PathVariable Long materiaId) {
        return materiaRepository.findById(materiaId)
                .map(materia -> {
                    List<UsuarioResponseDTO> integrantesDto = materia.getIntegrantes().stream()
                            .map(this::convertUsuarioToDto)
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(integrantesDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
