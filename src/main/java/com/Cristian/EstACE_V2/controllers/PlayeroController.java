package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.PlayeroRequest;
import com.Cristian.EstACE_V2.dtos.PlayeroResponse;
import com.Cristian.EstACE_V2.services.PlayeroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playeros")
@RequiredArgsConstructor
public class PlayeroController {

    private final PlayeroService playeroService;

    @GetMapping
    public ResponseEntity<List<PlayeroResponse>> obtenerMisPlayeros(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(playeroService.obtenerMisPlayeros(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayeroResponse> obtenerPorId(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(playeroService.obtenerPorId(id, token));
    }

    @PostMapping
    public ResponseEntity<PlayeroResponse> crearPlayero(@RequestBody PlayeroRequest request, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(playeroService.crearPlayero(request, token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayeroResponse> editarPlayero(@PathVariable Integer id, @RequestBody PlayeroRequest request, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(playeroService.editarPlayero(id, request, token));
    }
}