package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.MateriaRequestDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.model.Materia;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.MateriaRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> listarMaterias() {
        return materiaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MateriaResponseDTO obtenerMateria(Long materiaId) {
        return materiaRepository.findById(materiaId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + materiaId));
    }

    @Transactional
    public MateriaResponseDTO crearMateria(MateriaRequestDTO materiaDto) {
        Materia nuevaMateria = new Materia();
        nuevaMateria.setNombre(materiaDto.getNombre());
        nuevaMateria.setCodigo(materiaDto.getCodigo());
        nuevaMateria.setAnio(materiaDto.getAnio());
        nuevaMateria.setDescripcion(materiaDto.getDescripcion());
        Materia materiaGuardada = materiaRepository.save(nuevaMateria);
        return convertToDto(materiaGuardada);
    }

    @Transactional
    public MateriaResponseDTO actualizarMateria(Long materiaId, MateriaRequestDTO materiaDto) {
        Materia materiaExistente = findMateriaById(materiaId);
        materiaExistente.setNombre(materiaDto.getNombre());
        materiaExistente.setCodigo(materiaDto.getCodigo());
        materiaExistente.setAnio(materiaDto.getAnio());
        materiaExistente.setDescripcion(materiaDto.getDescripcion());
        Materia materiaActualizada = materiaRepository.save(materiaExistente);
        return convertToDto(materiaActualizada);
    }

    @Transactional
    public void eliminarMateria(Long materiaId) {
        Materia materia = findMateriaById(materiaId);
        if (!materia.getIntegrantes().isEmpty()) {
            throw new ConflictException("No se puede eliminar la materia porque tiene integrantes asignados.");
        }
        materiaRepository.deleteById(materiaId);
    }

    @Transactional
    public void asignarUsuarioAMateria(Long materiaId, Long usuarioId) {
        Materia materia = findMateriaById(materiaId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (materia.getIntegrantes().contains(usuario)) {
            throw new ConflictException("El usuario ya es integrante de la materia.");
        }

        materia.getIntegrantes().add(usuario);
        materiaRepository.save(materia);
    }

    @Transactional
    public void quitarUsuarioDeMateria(Long materiaId, Long usuarioId) {
        Materia materia = findMateriaById(materiaId);
        boolean removed = materia.getIntegrantes().removeIf(integrante -> integrante.getId().equals(usuarioId));
        if (!removed) {
            throw new ResourceNotFoundException("El usuario no es integrante de esta materia.");
        }
        materiaRepository.save(materia);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarIntegrantesDeMateria(Long materiaId) {
        Materia materia = findMateriaById(materiaId);
        return materia.getIntegrantes().stream()
                .map(this::convertUsuarioToDto)
                .collect(Collectors.toList());
    }

    private Materia findMateriaById(Long materiaId) {
        return materiaRepository.findById(materiaId)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + materiaId));
    }

    private Usuario findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
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

    private UsuarioResponseDTO convertUsuarioToDto(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().getNombre() : null);
        return dto;
    }
}

