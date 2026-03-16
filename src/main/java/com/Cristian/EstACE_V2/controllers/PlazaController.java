package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.PlazaLoteRequest;
import com.Cristian.EstACE_V2.dtos.PlazaRequest;
import com.Cristian.EstACE_V2.dtos.PlazaResponse;
import com.Cristian.EstACE_V2.entities.CategoriaVehiculo;
import com.Cristian.EstACE_V2.services.PlazaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plazas")
@RequiredArgsConstructor
public class PlazaController {

    private final PlazaService plazaService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaVehiculo>> obtenerCategorias() {
        return ResponseEntity.ok(plazaService.obtenerCategorias());
    }



    // (Opcional) Podemos mantener el filtrado por ID en el backend si en el futuro queremos paginación pesada
    // GET /api/plazas/estacionamiento/{estId}
//    @GetMapping("/estacionamiento/{estId}")
//    public ResponseEntity<List<PlazaResponse>> listarPlazasPorEstacionamiento(
//            @PathVariable Integer estId,
//            @RequestHeader("Authorization") String token) {
//        return ResponseEntity.ok(plazaService.listarPlazasPorEstacionamiento(estId, token));
//    }

    // GET /api/plazas -> Trae TODAS las plazas del dueño
    @GetMapping
    public ResponseEntity<List<PlazaResponse>> listarMisPlazas(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(plazaService.listarMisPlazas(token));
    }

    // POST /api/plazas -> Crear
    @PostMapping
    public ResponseEntity<PlazaResponse> crearPlaza(
            @RequestBody PlazaRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(plazaService.crearPlaza(request, token));
    }

    // POST /api/plazas/lote -> Crear múltiples plazas
    @PostMapping("/lote")
    public ResponseEntity<List<PlazaResponse>> crearPlazasLote(
            @RequestBody PlazaLoteRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(plazaService.crearPlazasEnLote(request, token));
    }

    // GET /api/plazas/{estId}/{plazaId}
    @GetMapping("/{estId}/{plazaId}")
    public ResponseEntity<PlazaResponse> obtenerPorId(
            @PathVariable Integer estId,
            @PathVariable Integer plazaId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(plazaService.obtenerPorId(estId, plazaId, token));
    }

    // PUT /api/plazas/{estId}/{plazaId} -> Editar
    @PutMapping("/{estId}/{plazaId}")
    public ResponseEntity<PlazaResponse> editarPlaza(
            @PathVariable Integer estId,
            @PathVariable Integer plazaId,
            @RequestBody PlazaRequest request,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(plazaService.editarPlaza(estId, plazaId, request, token));
    }
}