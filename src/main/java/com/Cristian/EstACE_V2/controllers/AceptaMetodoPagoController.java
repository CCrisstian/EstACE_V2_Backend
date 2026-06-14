package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.AceptaMetodoPagoRequest;
import com.Cristian.EstACE_V2.dtos.AceptaMetodoPagoResponse;
import com.Cristian.EstACE_V2.dtos.EstacionamientoResponseDTO;
import com.Cristian.EstACE_V2.entities.MetodoDePago;
import com.Cristian.EstACE_V2.services.AceptaMetodoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metodos-pago")
@RequiredArgsConstructor
public class AceptaMetodoPagoController {

    private final AceptaMetodoPagoService service;

    // GET /api/metodos-pago/catalogo -> Devuelve: Efectivo, Tarjeta, MercadoPago, etc.
    @GetMapping("/catalogo")
    public ResponseEntity<List<MetodoDePago>> obtenerCatalogo() {
        return ResponseEntity.ok(service.obtenerCatalogoMetodos());
    }

    // Devolvemos los Estacionamientos del usuario

    @GetMapping("/mis-estacionamientos")
    public ResponseEntity<List<EstacionamientoResponseDTO>> listarMisEstacionamientos(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.listarMisEstacionamientosActivos(token));
    }

    // GET /api/metodos-pago -> Puede recibir ?estId=X opcionalmente
    @GetMapping
    public ResponseEntity<List<AceptaMetodoPagoResponse>> listar(
            @RequestParam(required = false) Integer estId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.listarMisMetodosDePago(token, estId));
    }

    // POST /api/metodos-pago -> Crear nuevo
    @PostMapping
    public ResponseEntity<AceptaMetodoPagoResponse> crear(
            @RequestBody AceptaMetodoPagoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.crearMetodo(request, token));
    }

    // PUT /api/metodos-pago/{estId}/{metodoId} -> Editar existente
    @PutMapping("/{estId}/{metodoId}")
    public ResponseEntity<AceptaMetodoPagoResponse> editar(
            @PathVariable Integer estId,
            @PathVariable Integer metodoId,
            @RequestBody AceptaMetodoPagoRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.editarMetodo(estId, metodoId, request, token));
    }

    // DELETE /api/metodos-pago/{estId}/{metodoId} -> Quitar método
    @DeleteMapping("/{estId}/{metodoId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer estId,
            @PathVariable Integer metodoId,
            @RequestHeader("Authorization") String token) {
        service.eliminarMetodo(estId, metodoId, token);
        return ResponseEntity.noContent().build();
    }
}