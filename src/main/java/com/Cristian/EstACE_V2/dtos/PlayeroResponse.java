package com.Cristian.EstACE_V2.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayeroResponse {
    private Integer legajo;
    private Integer dni;
    private String nombre;
    private String apellido;

    // --- CAMPOS DE CONTACTO ---
    private String email;
    private String telefono;
    private String direccion;
    // ---------------------------------

    private String nombreEstacionamiento;
    private Integer estacionamientoId;
    private Boolean activo;

    private String avatarUrl;
}