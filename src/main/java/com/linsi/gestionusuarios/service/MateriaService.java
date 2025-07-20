package com.linsi.gestionusuarios.service;

import com.linsi.gestionusuarios.dto.MateriaRequestDTO;
import com.linsi.gestionusuarios.dto.MateriaResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.MateriaMapper;
import com.linsi.gestionusuarios.mapper.UsuarioMapper;
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
    private final MateriaMapper materiaMapper;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public List<MateriaResponseDTO> listarMaterias() {
        return materiaRepository.findAll().stream()
                .map(materiaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MateriaResponseDTO obtenerMateria(Long materiaId) {
        return materiaRepository.findById(materiaId)
                .map(materiaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + materiaId));
    }

    @Transactional
    public MateriaResponseDTO crearMateria(MateriaRequestDTO materiaDto) {
        Materia nuevaMateria = materiaMapper.toEntity(materiaDto);
        Materia materiaGuardada = materiaRepository.save(nuevaMateria);
        return materiaMapper.toDto(materiaGuardada);
    }

    @Transactional
    public MateriaResponseDTO actualizarMateria(Long materiaId, MateriaRequestDTO materiaDto) {
        Materia materiaExistente = findMateriaById(materiaId);
        materiaExistente.setNombre(materiaDto.getNombre());
        materiaExistente.setCodigo(materiaDto.getCodigo());
        materiaExistente.setAnio(materiaDto.getAnio());
        materiaExistente.setDescripcion(materiaDto.getDescripcion());
        Materia materiaActualizada = materiaRepository.save(materiaExistente);
        return materiaMapper.toDto(materiaActualizada);
    }

    @Transactional
    public void eliminarMateria(Long materiaId) {
        Materia materia = findMateriaById(materiaId);        
        for (Usuario integrante : new java.util.HashSet<>(materia.getIntegrantes())) {
            integrante.getMaterias().remove(materia);
        }
        materia.getIntegrantes().clear();
        materiaRepository.delete(materia);
    }

    @Transactional
    public void asignarUsuarioAMateria(Long materiaId, Long usuarioId) {
        Materia materia = findMateriaById(materiaId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (materia.getIntegrantes().contains(usuario)) {
            throw new ConflictException("El usuario ya es integrante de la materia.");
        }

        materia.getIntegrantes().add(usuario);
        usuario.getMaterias().add(materia);
    }

    @Transactional
    public void quitarUsuarioDeMateria(Long materiaId, Long usuarioId) {
        Materia materia = findMateriaById(materiaId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (materia.getIntegrantes().contains(usuario)) {
            materia.getIntegrantes().remove(usuario);
            usuario.getMaterias().remove(materia);
        } else {
            throw new ResourceNotFoundException("El usuario con ID " + usuarioId + " no es integrante de la materia con ID " + materiaId);
        }
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarIntegrantesDeMateria(Long materiaId) {
        Materia materia = findMateriaById(materiaId);
        return materia.getIntegrantes().stream()
                .map(usuarioMapper::toDto)
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
}

