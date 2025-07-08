package com.linsi.gestionusuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.BecaRequestDTO;
import com.linsi.gestionusuarios.dto.BecaResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
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

    @Transactional(readOnly = true)
    public List<BecaResponseDTO> listarBecas() {
        return becaRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BecaResponseDTO obtenerBeca(Long becaId) {
        return becaRepository.findById(becaId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Beca no encontrada con ID: " + becaId));
    }

    @Transactional
    public BecaResponseDTO crearBeca(BecaRequestDTO becaDto) {
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
        return convertToDto(becaActualizada);
    }

    @Transactional
    public void eliminarBeca(Long becaId) {
        if (!becaRepository.existsById(becaId)) {
            throw new ResourceNotFoundException("Beca no encontrada con ID: " + becaId);
        }
        becaRepository.deleteById(becaId);
    }

    @Transactional
    public BecaResponseDTO asignarUsuarioABeca(Long becaId, Long usuarioId) {
        Beca beca = findBecaById(becaId);
        Usuario usuario = findUsuarioById(usuarioId);

        if (beca.getUsuario() != null) {
            throw new ConflictException("La beca ya está asignada a otro usuario. Quite el usuario actual primero.");
        }

        beca.setUsuario(usuario);
        Beca becaGuardada = becaRepository.save(beca);
        return convertToDto(becaGuardada);
    }

    @Transactional
    public void quitarUsuarioDeBeca(Long becaId) {
        Beca beca = findBecaById(becaId);
        if (beca.getUsuario() == null) {
            throw new ResourceNotFoundException("La beca no tiene ningún usuario asignado.");
        }
        beca.setUsuario(null);
        becaRepository.save(beca);
    }

    private Beca findBecaById(Long becaId) {
        return becaRepository.findById(becaId)
                .orElseThrow(() -> new ResourceNotFoundException("Beca no encontrada con ID: " + becaId));
    }

    private Usuario findUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
    }

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
}
