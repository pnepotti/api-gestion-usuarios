package com.linsi.gestionusuarios.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.linsi.gestionusuarios.dto.AreaRequestDTO;
import com.linsi.gestionusuarios.dto.AreaResponseDTO;
import com.linsi.gestionusuarios.dto.ProyectoRequestDTO;
import com.linsi.gestionusuarios.dto.ProyectoResponseDTO;
import com.linsi.gestionusuarios.dto.UsuarioRegistroDTO;
import com.linsi.gestionusuarios.dto.UsuarioResponseDTO;
import com.linsi.gestionusuarios.exception.ConflictException;
import com.linsi.gestionusuarios.exception.DniAlreadyExistsException;
import com.linsi.gestionusuarios.exception.EmailAlreadyExistsException;
import com.linsi.gestionusuarios.exception.LegajoAlreadyExistsException;
import com.linsi.gestionusuarios.exception.ResourceNotFoundException;
import com.linsi.gestionusuarios.mapper.AreaMapper;
import com.linsi.gestionusuarios.mapper.ProyectoMapper;
import com.linsi.gestionusuarios.mapper.UsuarioMapper;
import com.linsi.gestionusuarios.model.Area;
import com.linsi.gestionusuarios.model.Proyecto;
import com.linsi.gestionusuarios.model.Usuario;
import com.linsi.gestionusuarios.repository.AreaRepository;
import com.linsi.gestionusuarios.repository.ProyectoRepository;
import com.linsi.gestionusuarios.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final AreaMapper areaMapper;
    private final ProyectoRepository proyectoRepository;
    private final ProyectoMapper proyectoMapper;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AreaResponseDTO crearArea(AreaRequestDTO areaDto) {
        if (areaRepository.existsByNombreIgnoreCase(areaDto.getNombre())) {
            throw new ConflictException("Ya existe un área con el nombre: " + areaDto.getNombre());
        }
        Area nuevaArea = areaMapper.toEntity(areaDto);
        Area areaGuardada = areaRepository.save(nuevaArea);
        return areaMapper.toDto(areaGuardada);
    }

    @Transactional(readOnly = true)
    public AreaResponseDTO obtenerArea(Long areaId) {
        return areaRepository.findById(areaId)
                .map(areaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + areaId));
    }

    @Transactional(readOnly = true)
    public List<AreaResponseDTO> listarAreas() {
        return areaRepository.findAll().stream()
                .map(areaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarArea(Long areaId) {
        Area area = areaRepository.findById(areaId)
            .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + areaId));
        for (Usuario usuario : new java.util.HashSet<>(area.getUsuarios())) {
            usuario.setArea(null);
        }
        for (Proyecto proyecto : new java.util.HashSet<>(area.getProyectos())) {
            proyecto.setArea(null);
        }
        area.getUsuarios().clear();
        area.getProyectos().clear();
        areaRepository.delete(area);
    }

    @Transactional
    public AreaResponseDTO actualizarArea(Long areaId, AreaRequestDTO areaDto) {
        Area areaExistente = areaRepository.findById(areaId)
            .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + areaId));

        if (areaRepository.existsByNombreIgnoreCaseAndIdNot(areaDto.getNombre(), areaId)) {
            throw new ConflictException("Ya existe un área con el nombre: " + areaDto.getNombre());
        }

        areaExistente.setNombre(areaDto.getNombre());
        areaExistente.setDescripcion(areaDto.getDescripcion());
        Area areaActualizada = areaRepository.save(areaExistente);
        return areaMapper.toDto(areaActualizada);
    }

    @Transactional
    public ProyectoResponseDTO crearYAsociarProyectoAlArea(Long areaId, ProyectoRequestDTO proyectoDto) {
        Area area = areaRepository.findById(areaId)
            .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + areaId));

        Proyecto nuevoProyecto = proyectoMapper.toEntity(proyectoDto);
        nuevoProyecto.setArea(area);
        Proyecto proyectoCreado = proyectoRepository.save(nuevoProyecto);
        return proyectoMapper.toDto(proyectoCreado);
    }

    @Transactional(readOnly = true)
    public Page<ProyectoResponseDTO> listarProyectosPorArea(Long areaId, Pageable pageable) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Área no encontrada con ID: " + areaId);
        }

        return proyectoRepository.findByArea_Id(areaId, pageable)
            .map(proyectoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarUsuariosPorArea(Long areaId, Pageable pageable) {
        if (!areaRepository.existsById(areaId)) {
            throw new ResourceNotFoundException("Área no encontrada con ID: " + areaId);
        }

        return usuarioRepository.findByArea_Id(areaId, pageable)
            .map(usuarioMapper::toDto);
    }

    @Transactional
    public UsuarioResponseDTO crearYAsociarUsuarioAlArea(Long areaId, UsuarioRegistroDTO usuarioDto) {
        Area area = areaRepository.findById(areaId)
            .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada con ID: " + areaId));

        if (usuarioRepository.existsByEmail(usuarioDto.getEmail())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con ese email");
        }
        if (usuarioRepository.existsByDni(usuarioDto.getDni())) {
            throw new DniAlreadyExistsException("Ya existe un usuario con ese DNI");
        }
        if (usuarioDto.getLegajo() != null && usuarioRepository.existsByLegajo(usuarioDto.getLegajo())) {
            throw new LegajoAlreadyExistsException("Ya existe un usuario con ese legajo");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .nombre(usuarioDto.getNombre())
                .apellido(usuarioDto.getApellido())
                .dni(usuarioDto.getDni())
                .email(usuarioDto.getEmail())
                .telefono(usuarioDto.getTelefono() != null ? usuarioDto.getTelefono() : null)
                .direccion(usuarioDto.getDireccion() != null ? usuarioDto.getDireccion() : null)
                .legajo(usuarioDto.getLegajo() != null ? usuarioDto.getLegajo() : null)
                .password(passwordEncoder.encode(usuarioDto.getPassword()))
                .area(area)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
        return usuarioMapper.toDto(usuarioGuardado);
    }
}
