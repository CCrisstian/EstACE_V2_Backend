package com.Cristian.EstACE_V2.services;

import com.Cristian.EstACE_V2.dtos.EstacionamientoRequest;
import com.Cristian.EstACE_V2.entities.Dueño;
import com.Cristian.EstACE_V2.entities.Estacionamiento;
import com.Cristian.EstACE_V2.repositories.DueñoRepository;
import com.Cristian.EstACE_V2.repositories.EstacionamientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstacionamientoService {

    @Autowired
    private EstacionamientoRepository estacionamientoRepository;

    @Autowired
    private DueñoRepository duenoRepository;

    // --- 1. LISTAR (Read) ---
    public List<Estacionamiento> obtenerPorDueño(Integer duenoLegajo) {
        return estacionamientoRepository.findByDuenoId(duenoLegajo);
    }

    public Estacionamiento obtenerPorId(Integer id) {
        return estacionamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estacionamiento no encontrado"));
    }

    // --- 2. CREAR (Create) ---
    @Transactional
    public Estacionamiento crearEstacionamiento(Integer duenoLegajo, EstacionamientoRequest request) {
        // Validar Dueño
        Dueño dueno = duenoRepository.findById(duenoLegajo)
                .orElseThrow(() -> new RuntimeException("Dueño no encontrado (Legajo inválido)"));

        // Validar Duplicados
        if (estacionamientoRepository.existsByProvinciaAndLocalidadAndDireccion(
                request.getProvincia(), request.getLocalidad(), request.getDireccion()) ||
                estacionamientoRepository.existsByLatitudAndLongitud(request.getLatitud(), request.getLongitud())) {
            throw new RuntimeException("Ya existe un estacionamiento en esa ubicación/coordenadas.");
        }

        // Mapeo Request -> Entidad
        Estacionamiento nuevo = Estacionamiento.builder()
                .dueno(dueno)
                .nombre(request.getNombre())
                .provincia(request.getProvincia())
                .localidad(request.getLocalidad())
                .direccion(request.getDireccion())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .diasAtencion(request.getDiasAtencion())
                .hraAtencion(request.getHraAtencion())
                .diasFeriadoAtencion(request.getDiasFeriadoAtencion())
                .finDeSemanaAtencion(request.getFinDeSemanaAtencion())
                .horaFinDeSemana(request.getHoraFinDeSemana())
                .disponibilidad(request.getDisponibilidad() != null ? request.getDisponibilidad() : true)
                .puntaje(0.0)
                .puntajeAcumulado(0.0)
                .cantidadVotos(0)
                .build();

        return estacionamientoRepository.save(nuevo);
    }

    // --- 3. EDITAR (Update) ---
    @Transactional
    public Estacionamiento editarEstacionamiento(Integer id, Integer duenoLegajo, EstacionamientoRequest request) {
        Estacionamiento est = obtenerPorId(id);

        // Validar que el dueño sea el propietario del estacionamiento
        if (!est.getDueno().getId().equals(duenoLegajo)) {
            throw new RuntimeException("No tienes permiso para editar este estacionamiento.");
        }

        // Validar duplicados (excluyendo el actual)
        boolean existeOtro = estacionamientoRepository.existsDuplicadoParaEditar(
                id, request.getProvincia(), request.getLocalidad(), request.getDireccion(),
                request.getLatitud(), request.getLongitud()
        );

        if (existeOtro) {
            throw new RuntimeException("Los nuevos datos coinciden con otro estacionamiento ya registrado.");
        }

        // Actualizar campos
        est.setNombre(request.getNombre());
        est.setProvincia(request.getProvincia());
        est.setLocalidad(request.getLocalidad());
        est.setDireccion(request.getDireccion());
        est.setLatitud(request.getLatitud());
        est.setLongitud(request.getLongitud());
        est.setDiasAtencion(request.getDiasAtencion());
        est.setHraAtencion(request.getHraAtencion());
        est.setDiasFeriadoAtencion(request.getDiasFeriadoAtencion());
        est.setFinDeSemanaAtencion(request.getFinDeSemanaAtencion());
        est.setHoraFinDeSemana(request.getHoraFinDeSemana());

        // Aquí también podemos actualizar disponibilidad si viene en el request
        if (request.getDisponibilidad() != null) {
            est.setDisponibilidad(request.getDisponibilidad());
        }

        return estacionamientoRepository.save(est);
    }
}