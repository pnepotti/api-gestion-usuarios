package com.linsi.gestionusuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.BecaMapper;
import com.linsi.gestionusuarios.model.Beca;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.BecaRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BecaService {

    private final BecaRepository becaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BecaMapper becaMapper;

    @Transactional(readOnly = true)
    public List<BecaResponseDTO> listarBecas() {
        return becaRepository.findAll().stream()
                .map(becaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BecaResponseDTO obtenerBeca(Long becaId) {
        return becaRepository.findById(becaId)
                .map(becaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Beca no encontrada con ID: " + becaId));
    }

    @Transactional
    public BecaResponseDTO crearBecaParaUsuario(Long usuarioId, BecaRequestDTO becaDto) {

        Usuario usuario = findUsuarioById(usuarioId);

        Beca nuevaBeca = becaMapper.toEntity(becaDto);
        nuevaBeca.setUsuario(usuario);
        usuario.getBecas().add(nuevaBeca);
        
        Beca becaGuardada = becaRepository.save(nuevaBeca);
        return becaMapper.toDto(becaGuardada);
    }

    @Transactional
    public BecaResponseDTO actualizarBeca(Long becaId, BecaRequestDTO becaDto) {
        Beca becaExistente = findBecaById(becaId);
        becaExistente.setNombre(becaDto.getNombre());
        becaExistente.setTipo(becaDto.getTipo());   
        becaExistente.setMonto(becaDto.getMonto());
        becaExistente.setFechaInicio(becaDto.getFechaInicio());
        becaExistente.setFechaFin(becaDto.getFechaFin());
        becaExistente.setDuracion(becaDto.getDuracion());
        Beca becaActualizada = becaRepository.save(becaExistente);
        return becaMapper.toDto(becaActualizada);
    }

    @Transactional
    public void eliminarBeca(Long becaId) {
        Beca beca = findBecaById(becaId);

        // Si la beca está asociada a un usuario, la desvinculamos primero para mantener la consistencia.
        if (beca.getUsuario() != null) {
            beca.getUsuario().getBecas().remove(beca);
        }
        becaRepository.delete(beca);
    }

    @Transactional
    public void eliminarBecasDeUsuario(Long usuarioId) {
        List<Beca> becas = becaRepository.findByUsuarioId(usuarioId);
        if (!becas.isEmpty()) {
            becaRepository.deleteAll(becas);
        }
    }

    @Transactional
    public void asignarUsuarioABeca(Long becaId, Long usuarioId) {
        Beca beca = findBecaById(becaId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (beca.getUsuario() != null) {
            throw new ConflictException("La beca ya está asignada a otro usuario. Quite el usuario actual primero.");
        }

        beca.setUsuario(usuario);
        usuario.getBecas().add(beca);
    }

    @Transactional
    public void quitarUsuarioDeBeca(Long becaId) {
        Beca beca = findBecaById(becaId);
        Usuario usuario = beca.getUsuario();
        if (usuario == null) {
            throw new ResourceNotFoundException("La beca no tiene ningún usuario asignado.");
        }
        beca.setUsuario(null);
        usuario.getBecas().remove(beca);
    }

    @Transactional(readOnly = true)
    public List<BecaResponseDTO> listarBecasPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }
        return becaRepository.findByUsuarioId(usuarioId).stream()
                .map(becaMapper::toDto)
                .collect(Collectors.toList());
    }

    private Beca findBecaById(Long becaId) {
        return becaRepository.findById(becaId)
                .orElseThrow(() -> new ResourceNotFoundException("Beca no encontrada con ID: " + becaId));
    }

    private Usuario findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }
}
