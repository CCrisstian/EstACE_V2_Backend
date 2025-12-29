package com.Cristian.EstACE_V2.controllers;

import com.Cristian.EstACE_V2.dtos.LoginRequest;
import com.Cristian.EstACE_V2.dtos.UsuarioResponse;
import com.Cristian.EstACE_V2.entities.Usuario;
import com.Cristian.EstACE_V2.security.JwtService;
import com.Cristian.EstACE_V2.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.Cristian.EstACE_V2.dtos.UsuarioUpdateRequest; // Importar
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager; // Verifica credenciales

    @Autowired
    private JwtService jwtService; // Crea el token

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. Autenticamos con Spring Security (Lanzará excepción si falla)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getLegajo().toString(),
                        loginRequest.getPassword()
                )
        );

        // 2. Si pasa, buscamos el usuario para obtener sus datos
        Usuario usuario = usuarioService.buscarPorLegajo(loginRequest.getLegajo());

        // 3. Generamos el Token
        String jwtToken = jwtService.generateToken(usuario.getUsername());

        // 4. Preparamos la respuesta
        UsuarioResponse response = new UsuarioResponse();
        response.setLegajo(usuario.getUsuLegajo());
        response.setDni(usuario.getUsuDni());
        response.setNombre(usuario.getUsuNom());
        response.setApellido(usuario.getUsuAp());
        response.setTipo(usuario.getUsuTipo());
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

    // GET: Ver mi propio perfil
    @GetMapping("/perfil")
    public ResponseEntity<?> verMiPerfil() {
        // 1. Obtener el usuario autenticado del contexto de seguridad (del Token)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajoString = auth.getName(); // En UserDetails el username es el legajo
        Integer legajo = Integer.parseInt(legajoString);

        // 2. Buscar datos
        Usuario usuario = usuarioService.buscarPorLegajo(legajo);

        // 3. Mapear a Response (SIN PASSWORD)
        UsuarioResponse response = new UsuarioResponse();
        response.setLegajo(usuario.getUsuLegajo());
        response.setDni(usuario.getUsuDni());
        response.setNombre(usuario.getUsuNom());
        response.setApellido(usuario.getUsuAp());
        response.setTipo(usuario.getUsuTipo());
        // response.setToken(...) -> No es necesario devolver token aquí, ya lo tiene.

        return ResponseEntity.ok(response);
    }

    // PUT: Editar mi propio perfil
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarMiPerfil(@Valid @RequestBody UsuarioUpdateRequest request) {
        try {
            // 1. Obtener ID del token (Nuevamente, por seguridad)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Integer legajo = Integer.parseInt(auth.getName());

            // 2. Llamar al servicio
            Usuario usuarioActualizado = usuarioService.actualizarPerfil(legajo, request);

            // 3. Responder con los datos nuevos
            UsuarioResponse response = new UsuarioResponse();
            response.setLegajo(usuarioActualizado.getUsuLegajo());
            response.setDni(usuarioActualizado.getUsuDni());
            response.setNombre(usuarioActualizado.getUsuNom());
            response.setApellido(usuarioActualizado.getUsuAp());
            response.setTipo(usuarioActualizado.getUsuTipo());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}