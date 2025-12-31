package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.EstacionamientoRequest;
import com.Cristian.EstACE_V2.entities.Estacionamiento;
import com.Cristian.EstACE_V2.services.EstacionamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estacionamientos")
public class EstacionamientoController {

    @Autowired
    private EstacionamientoService estacionamientoService;

    // Helper para obtener el ID del usuario autenticado
    private Integer getUsuarioAutenticadoId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Integer.parseInt(username); // En tu UserDetails, el username es el legajo
    }

    // --- 1. LISTAR MIS ESTACIONAMIENTOS ---
    // Endpoint: GET /api/estacionamientos/mis-estacionamientos
    @GetMapping("/mis-estacionamientos")
    public ResponseEntity<List<Estacionamiento>> listarMisEstacionamientos() {
        Integer id = getUsuarioAutenticadoId();
        return ResponseEntity.ok(estacionamientoService.obtenerPorDueño(id));
    }

    // --- 2. OBTENER UNO POR ID ---
    // Endpoint: GET /api/estacionamientos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Estacionamiento> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(estacionamientoService.obtenerPorId(id));
    }

    // --- 3. CREAR NUEVO ---
    // Endpoint: POST /api/estacionamientos
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody EstacionamientoRequest request) {
        try {
            Integer id = getUsuarioAutenticadoId();
            Estacionamiento nuevo = estacionamientoService.crearEstacionamiento(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- 4. EDITAR EXISTENTE ---
    // Endpoint: PUT /api/estacionamientos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody EstacionamientoRequest request) {
        try {
            Integer idDueño = getUsuarioAutenticadoId();
            Estacionamiento editado = estacionamientoService.editarEstacionamiento(id, idDueño, request);
            return ResponseEntity.ok(editado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}