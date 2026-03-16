package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.config.JwtService;
import com.Cristian.EstACE_V2.dtos.PlazaLoteRequest;
import com.Cristian.EstACE_V2.dtos.PlazaRequest;
import com.Cristian.EstACE_V2.dtos.PlazaResponse;
import com.Cristian.EstACE_V2.entities.CategoriaVehiculo;
import com.Cristian.EstACE_V2.entities.Estacionamiento;
import com.Cristian.EstACE_V2.entities.Plaza;
import com.Cristian.EstACE_V2.entities.PlazaId;
import com.Cristian.EstACE_V2.repositories.CategoriaVehiculoRepository;
import com.Cristian.EstACE_V2.repositories.EstacionamientoRepository;
import com.Cristian.EstACE_V2.repositories.PlazaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlazaService {

    private final PlazaRepository plazaRepository;
    private final EstacionamientoRepository estacionamientoRepository;
    private final JwtService jwtService;

    private final CategoriaVehiculoRepository categoriaVehiculoRepository;

    public List<PlazaResponse> listarMisPlazas(String token) {
        Integer legajoUsuario = jwtService.extractLegajo(token.substring(7));

        // 1. Buscar todos los estacionamientos de este dueño
        List<Estacionamiento> misEstacionamientos = estacionamientoRepository.findByDuenoId(legajoUsuario);

        if (misEstacionamientos.isEmpty()) {
            return List.of(); // Si no tiene estacionamientos, devolvemos lista vacía
        }

        // 2. Extraer los ID de esos estacionamientos
        List<Integer> misEstIds = misEstacionamientos.stream()
                .map(Estacionamiento::getId)    // 1. EXTRACCIÓN: Sacar el ID
                .collect(Collectors.toList());  // 2. RECOLECCIÓN: Guardar en una lista

        // 3. Buscar todas las plazas que pertenezcan a esos ID
        List<Plaza> todasMisPlazas = plazaRepository.findByEstIdIn(misEstIds);

        return todasMisPlazas.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private PlazaResponse mapToResponse(Plaza plaza) {
        return PlazaResponse.builder()
                .estId(plaza.getEstId())
                .plazaId(plaza.getPlazaId())
                .categoriaId(plaza.getCategoriaId())
                .nombre(plaza.getNombre())
                .tipo(plaza.getTipo())
                .disponibilidad(plaza.getDisponibilidad())
                .build();
    }

    // --- OBTENER CATEGORÍAS (Para el frontend) ---
    public List<CategoriaVehiculo> obtenerCategorias() {
        return categoriaVehiculoRepository.findAll();
    }

    // --- CREAR PLAZA ---
    @Transactional
    public PlazaResponse crearPlaza(PlazaRequest request, String token) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        Estacionamiento est = estacionamientoRepository.findById(request.getEstId())
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDisponibilidad()) {
            throw new RuntimeException("No se pueden agregar plazas a un estacionamiento inactivo.");
        }

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso para agregar plazas a este estacionamiento");
        }

        // --- CALCULAR EL NUEVO ID MANUALMENTE ---
        Integer maxPlazaId = plazaRepository.findMaxPlazaIdByEstId(request.getEstId());
        // Si maxPlazaId es null (no hay plazas aún), empezamos en 1. Si no, sumamos 1 al máximo.
        Integer nuevoPlazaId = (maxPlazaId == null ? 0 : maxPlazaId) + 1;

        // 2. Crear la Plaza
        Plaza nuevaPlaza = Plaza.builder()
                .estId(request.getEstId())
                .plazaId(nuevoPlazaId)
                .categoriaId(request.getCategoriaId())
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .disponibilidad(request.getDisponibilidad() != null ? request.getDisponibilidad() : true)
                .build();

        nuevaPlaza = plazaRepository.save(nuevaPlaza);
        return mapToResponse(nuevaPlaza);
    }

    // --- CREAR PLAZAS EN LOTE (MASIVO) ---
    @Transactional
    public List<PlazaResponse> crearPlazasEnLote(PlazaLoteRequest request, String token) {

        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        Estacionamiento est = estacionamientoRepository.findById(request.getEstId())
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDisponibilidad()) {
            throw new RuntimeException("No se pueden agregar plazas a un estacionamiento inactivo.");
        }

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso para agregar plazas a este estacionamiento");
        }

        // 1. VALIDAR NOMBRES REPETIDOS
        List<Plaza> plazasExistentes = plazaRepository.findByEstIdAndNombreIn(request.getEstId(), request.getNombres());

        if (!plazasExistentes.isEmpty()) {
            List<String> nombresRepetidos = plazasExistentes.stream()
                    .map(Plaza::getNombre)
                    .collect(Collectors.toList());
            throw new RuntimeException("Los siguientes nombres ya existen en el estacionamiento: " + String.join(", ", nombresRepetidos));
        }

        // 2. OBTENER EL ID INICIAL
        Integer maxPlazaId = plazaRepository.findMaxPlazaIdByEstId(request.getEstId());

        int siguienteId = (maxPlazaId == null ? 0 : maxPlazaId) + 1;

        // 3. ARMAR EL LOTE DE PLAZAS
        List<Plaza> nuevasPlazas = new ArrayList<>();

        for (String nombrePlaza : request.getNombres()) {
            Plaza p = Plaza.builder()
                    .estId(request.getEstId())
                    .plazaId(siguienteId++) // Asigna y luego suma 1 para la siguiente vuelta
                    .categoriaId(request.getCategoriaId())
                    .nombre(nombrePlaza)
                    .tipo(request.getTipo())
                    .disponibilidad(request.getDisponibilidad() != null ? request.getDisponibilidad() : true)
                    .build();
            nuevasPlazas.add(p);
        }

        // 4. GUARDAR TODO DE GOLPE Y DEVOLVER
        nuevasPlazas = plazaRepository.saveAll(nuevasPlazas);

        return nuevasPlazas.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- OBTENER POR ID ---
    @Transactional(readOnly = true)
    public PlazaResponse obtenerPorId(Integer estId, Integer plazaId, String token) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        Estacionamiento est = estacionamientoRepository.findById(estId)
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso");
        }

        PlazaId idCompuesto = new PlazaId(estId, plazaId);
        Plaza plaza = plazaRepository.findById(idCompuesto)
                .orElseThrow(() -> new RuntimeException("Plaza no encontrada"));

        return mapToResponse(plaza);
    }

    // --- EDITAR PLAZA ---
    @Transactional
    public PlazaResponse editarPlaza(Integer estId, Integer plazaId, PlazaRequest request, String token) {

        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        // 1. Validar Dueño
        Estacionamiento est = estacionamientoRepository.findById(estId)
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso para editar esta plaza");
        }

        // 2. Buscar la Plaza con la Clave Compuesta
        PlazaId idCompuesto = new PlazaId(estId, plazaId);

        Plaza plaza = plazaRepository.findById(idCompuesto)
                .orElseThrow(() -> new RuntimeException("Plaza no encontrada"));

        // 3. Actualizar datos (NO permitimos cambiar estId aquí por seguridad)
        plaza.setCategoriaId(request.getCategoriaId());
        plaza.setNombre(request.getNombre());
        plaza.setTipo(request.getTipo());
        if (request.getDisponibilidad() != null) {
            plaza.setDisponibilidad(request.getDisponibilidad());
        }

        plazaRepository.save(plaza);
        return mapToResponse(plaza);
    }
}