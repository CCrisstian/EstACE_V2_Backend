package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.config.JwtService;
import com.Cristian.EstACE_V2.dtos.PlayeroRequest;
import com.Cristian.EstACE_V2.dtos.PlayeroResponse;
import com.Cristian.EstACE_V2.entities.Estacionamiento;
import com.Cristian.EstACE_V2.entities.Playero;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.repositories.EstacionamientoRepository;
import com.Cristian.EstACE_V2.repositories.PlayeroRepository;
import com.Cristian.EstACE_V2.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayeroService {

    private final PlayeroRepository playeroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstacionamientoRepository estacionamientoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // --- LISTAR ---
    @Transactional(readOnly = true)
    public List<PlayeroResponse> obtenerMisPlayeros(String token) {
        Integer legajoDueño = jwtService.extractLegajo(token.substring(7));

        List<Playero> playeros = playeroRepository.findByDueñoLegajo(legajoDueño);

        return playeros.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- CREAR ---
    @Transactional
    public PlayeroResponse crearPlayero(PlayeroRequest request, String token) {
        Integer legajoDueño = jwtService.extractLegajo(token.substring(7));

        // 1. Validar DNI único
        if (usuarioRepository.existsByUsuDni(request.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese DNI.");
        }

        // 2. Validar Estacionamiento y Contraseña
        Estacionamiento estacionamiento = estacionamientoRepository.findById(request.getEstacionamientoId())
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!estacionamiento.getDueno().getId().equals(legajoDueño)) {
            throw new RuntimeException("No tienes permiso para asignar playeros a este estacionamiento.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria para registrar un nuevo playero.");
        }

        // 3. Crear Usuario (Base)
        Usuario nuevoUsuario = Usuario.builder()
                .usuDni(request.getDni())
                .usuNom(request.getNombre())
                .usuAp(request.getApellido())
                .usuEmail(request.getEmail())
                .usuTelefono(request.getTelefono())
                .usuDireccion(request.getDireccion())
                .usuPass(passwordEncoder.encode(request.getPassword())) // Encriptar pass
                .usuTipo("Playero")
                .usuAvatarUrl(request.getAvatarUrl())
                .build();

        // Guardamos usuario para generar el Legajo (ID)
        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        // 4. Crear Playero (Vinculado)
        Playero nuevoPlayero = Playero.builder()
                .usuario(nuevoUsuario) // @MapsId usará el ID de este usuario
                .estacionamiento(estacionamiento)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        nuevoPlayero = playeroRepository.save(nuevoPlayero);

        return mapToResponse(nuevoPlayero);
    }

    // --- EDITAR ---
    @Transactional
    public PlayeroResponse editarPlayero(Integer id, PlayeroRequest request, String token) {
        Integer legajoDueño = jwtService.extractLegajo(token.substring(7));

        Playero playero = playeroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playero no encontrado"));

        // Validar seguridad: El playero debe pertenecer a un estacionamiento del dueño logueado
        if (!playero.getEstacionamiento().getDueno().getId().equals(legajoDueño)) {
            throw new RuntimeException("No tienes permiso para editar este playero.");
        }

        // Actualizar datos de Usuario
        Usuario usuario = playero.getUsuario();
        usuario.setUsuDni(request.getDni());
        usuario.setUsuNom(request.getNombre());
        usuario.setUsuAp(request.getApellido());
        usuario.setUsuEmail(request.getEmail());
        usuario.setUsuTelefono(request.getTelefono());
        usuario.setUsuDireccion(request.getDireccion());

        if (request.getAvatarUrl() != null) {
            usuario.setUsuAvatarUrl(request.getAvatarUrl());
        }

        // Solo actualizamos contraseña si viene algo en el request
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setUsuPass(passwordEncoder.encode(request.getPassword()));
        }
        usuarioRepository.save(usuario);

        // Actualizar datos de Playero (Cambio de sucursal o estado)
        if (request.getEstacionamientoId() != null) {
            Estacionamiento nuevoEst = estacionamientoRepository.findById(request.getEstacionamientoId())
                    .orElseThrow(() -> new RuntimeException("Estacionamiento inválido"));

            // Validar que el nuevo estacionamiento también sea del dueño
            if (!nuevoEst.getDueno().getId().equals(legajoDueño)) {
                throw new RuntimeException("El estacionamiento destino no te pertenece.");
            }
            playero.setEstacionamiento(nuevoEst);
        }

        if (request.getActivo() != null) {
            playero.setActivo(request.getActivo());
        }

        playeroRepository.save(playero);

        return mapToResponse(playero);
    }

    // --- OBTENER POR ID ---
    @Transactional(readOnly = true)
    public PlayeroResponse obtenerPorId(Integer id, String token) {
        // Validación para editar
        Playero p = playeroRepository.findById(id).orElseThrow();
        return mapToResponse(p);
    }

    // Mapper auxiliar
    private PlayeroResponse mapToResponse(Playero p) {
        return PlayeroResponse.builder()
                .legajo(p.getId())
                .dni(p.getUsuario().getUsuDni())
                .nombre(p.getUsuario().getUsuNom())
                .apellido(p.getUsuario().getUsuAp())
                .email(p.getUsuario().getUsuEmail())
                .telefono(p.getUsuario().getUsuTelefono())
                .direccion(p.getUsuario().getUsuDireccion())
                .estacionamientoId(p.getEstacionamiento().getId())
                .nombreEstacionamiento(p.getEstacionamiento().getNombre())
                .activo(p.getActivo())
                .avatarUrl(p.getUsuario().getUsuAvatarUrl())
                .build();
    }
}