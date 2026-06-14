package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.config.JwtService;
import com.Cristian.EstACE_V2.dtos.AceptaMetodoPagoRequest;
import com.Cristian.EstACE_V2.dtos.AceptaMetodoPagoResponse;
import com.Cristian.EstACE_V2.dtos.EstacionamientoResponseDTO;
import com.Cristian.EstACE_V2.entities.AceptaMetodoDePago;
import com.Cristian.EstACE_V2.entities.AceptaMetodoDePagoId;
import com.Cristian.EstACE_V2.entities.Estacionamiento;
import com.Cristian.EstACE_V2.entities.MetodoDePago;
import com.Cristian.EstACE_V2.repositories.AceptaMetodoDePagoRepository;
import com.Cristian.EstACE_V2.repositories.EstacionamientoRepository;
import com.Cristian.EstACE_V2.repositories.MetodoDePagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AceptaMetodoPagoService {

    private final AceptaMetodoDePagoRepository aceptaMetodoDePagoRepository;
    private final EstacionamientoRepository estacionamientoRepository;
    private final MetodoDePagoRepository metodoDePagoRepository;
    private final JwtService jwtService;

    // --- OBTENER Métodos de Pago ---
    public List<MetodoDePago> obtenerCatalogoMetodos() {
        return metodoDePagoRepository.findAll();
    }


    // LISTAR Estacionamientos
    public List<EstacionamientoResponseDTO> listarMisEstacionamientosActivos(String token) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        List<Estacionamiento> misEstacionamientos = estacionamientoRepository.findByDuenoId(legajoDueno);

        return misEstacionamientos.stream()
                .filter(Estacionamiento::getDisponibilidad)
                .map(e -> new EstacionamientoResponseDTO(e.getId(), e.getNombre()))
                .collect(Collectors.toList());
    }

    // --- LISTAR Métodos de Pago Aceptados ---
    @Transactional(readOnly = true)
    public List<AceptaMetodoPagoResponse> listarMisMetodosDePago(String token, Integer estIdFiltro) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));
        List<AceptaMetodoDePago> resultados;

        if (estIdFiltro != null) {
            // Caso 1: El usuario seleccionó UN estacionamiento específico
            Estacionamiento est = estacionamientoRepository.findById(estIdFiltro)
                    .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));
            if (!est.getDueno().getId().equals(legajoDueno)) {
                throw new RuntimeException("No tienes permiso sobre este estacionamiento");
            }
            resultados = aceptaMetodoDePagoRepository.findByEstacionamientoId(estIdFiltro);
        } else {
            // Caso 2: Quiere ver TODOS sus estacionamientos
            List<Integer> misEstIds = estacionamientoRepository.findByDuenoId(legajoDueno)
                    .stream().map(Estacionamiento::getId).collect(Collectors.toList());

            if (misEstIds.isEmpty()) return List.of();

            resultados = aceptaMetodoDePagoRepository.findByEstacionamientoIdIn(misEstIds);
        }

        return resultados.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // --- CREAR MÉTODO DE PAGO (POST) ---
    @Transactional
    public AceptaMetodoPagoResponse crearMetodo(AceptaMetodoPagoRequest request, String token) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        Estacionamiento est = estacionamientoRepository.findById(request.getEstId())
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso sobre este Estacionamiento");
        }

        AceptaMetodoDePagoId id = new AceptaMetodoDePagoId(request.getEstId(), request.getMetodoPagoId());

        // Si ya existe, lanzamos error (Evita el "update involuntario")
        if (aceptaMetodoDePagoRepository.existsById(id)) {
            throw new RuntimeException("Este método de pago ya está asignado a este Estacionamiento.");
        }

        LocalDate fechaDesde = request.getDesde() != null ? request.getDesde() : LocalDate.now();
        if (request.getHasta() != null && request.getHasta().isBefore(fechaDesde)) {
            throw new RuntimeException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'.");
        }

        MetodoDePago metodo = metodoDePagoRepository.findById(request.getMetodoPagoId())
                .orElseThrow(() -> new RuntimeException("Método de pago inválido"));

        // Como no existe, creamos uno nuevo
        AceptaMetodoDePago nuevoAcepta = new AceptaMetodoDePago(id, est, metodo, fechaDesde, request.getHasta());
        nuevoAcepta = aceptaMetodoDePagoRepository.save(nuevoAcepta);

        return mapToResponse(nuevoAcepta);
    }

    // --- EDITAR MÉTODO DE PAGO (PUT) ---
    @Transactional
    public AceptaMetodoPagoResponse editarMetodo(Integer estId, Integer metodoPagoId, AceptaMetodoPagoRequest request, String token) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        Estacionamiento est = estacionamientoRepository.findById(estId)
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso sobre este estacionamiento");
        }

        AceptaMetodoDePagoId id = new AceptaMetodoDePagoId(estId, metodoPagoId);

        // Para editar, TIENE que existir primero
        AceptaMetodoDePago aceptaExistente = aceptaMetodoDePagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El método de pago no está asignado a este estacionamiento, no se puede editar."));

        LocalDate fechaDesde = request.getDesde() != null ? request.getDesde() : aceptaExistente.getDesde();
        if (request.getHasta() != null && request.getHasta().isBefore(fechaDesde)) {
            throw new RuntimeException("La fecha 'hasta' no puede ser anterior a la fecha 'desde'.");
        }

        // Solo actualizamos las fechas (hasta puede venir null, y lo guardará como indefinido)
        aceptaExistente.setDesde(fechaDesde);
        aceptaExistente.setHasta(request.getHasta());

        aceptaExistente = aceptaMetodoDePagoRepository.save(aceptaExistente);

        return mapToResponse(aceptaExistente);
    }

    // --- ELIMINAR, Quitar  método de Pago
    @Transactional
    public void eliminarMetodo(Integer estId, Integer metodoPagoId, String token) {
        Integer legajoDueno = jwtService.extractLegajo(token.substring(7));

        Estacionamiento est = estacionamientoRepository.findById(estId)
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));

        if (!est.getDueno().getId().equals(legajoDueno)) {
            throw new RuntimeException("No tienes permiso");
        }

        AceptaMetodoDePagoId id = new AceptaMetodoDePagoId(estId, metodoPagoId);
        aceptaMetodoDePagoRepository.deleteById(id);
    }

    // --- MAPPER ---
    private AceptaMetodoPagoResponse mapToResponse(AceptaMetodoDePago a) {
        return AceptaMetodoPagoResponse.builder()
                .estId(a.getEstacionamiento().getId())
                .estNombre(a.getEstacionamiento().getNombre())
                .metodoPagoId(a.getMetodoDePago().getId())
                .metodoPagoDescripcion(a.getMetodoDePago().getDescripcion())
                .desde(a.getDesde())
                .hasta(a.getHasta())
                .build();
    }
}