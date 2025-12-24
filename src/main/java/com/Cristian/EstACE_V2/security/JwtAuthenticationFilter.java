package com.Cristian.EstACE_V2.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLegajo; // El identificador que viene en el token

        // 1. Verificamos si la petición trae el header "Authorization: Bearer ..."
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Si no hay token, dejamos pasar (SecurityConfig decidirá si rechaza)
            return;
        }

        // 2. Extraemos el token puro (quitando la palabra "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Extraemos el Legajo (username) del token
        userLegajo = jwtService.extractUsername(jwt);

        // 4. Validación del usuario
        // Si hay legajo y el usuario NO está autenticado todavía en el contexto actual...
        if (userLegajo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Buscamos al usuario en la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userLegajo);

            // Validamos si el token es correcto para este usuario
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                // Creamos la "tarjeta de acceso" oficial de Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ¡Autorizado! Ponemos al usuario en el contexto
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}