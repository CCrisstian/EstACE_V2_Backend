package com.Cristian.EstACE_V2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desactivar CSRF: Es necesario para que funcionen las peticiones POST desde Postman
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configurar las reglas de acceso
                .authorizeHttpRequests(auth -> auth
                                // Permitir acceso PÚBLICO al endpoint de login
                                .requestMatchers("/api/usuarios/login").permitAll()

                                // Opción A: Permitir TODO lo demás por ahora (ideal para desarrollo rápido)
                                .anyRequest().permitAll()

                        // Opción B (Comentada): Si quisieras bloquear el resto, usarías esto:
                        // .anyRequest().authenticated()
                );
        return http.build();
    }
}
