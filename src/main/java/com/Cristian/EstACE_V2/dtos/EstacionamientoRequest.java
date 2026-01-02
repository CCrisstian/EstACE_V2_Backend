package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstacionamientoRequest {

    @NotNull(message = "El nombre del Estacionamiento es obligatorio")
    private String nombre;

    private String provincia;
    private String localidad;
    private String direccion;

    private Double latitud;
    private Double longitud;

    // Horarios combinados (ej: "Lunes a Viernes", "08:00 - 20:00")
    private String diasAtencion;
    private String hraAtencion;

    private Boolean diasFeriadoAtencion;
    private Boolean finDeSemanaAtencion;
    private String horaFinDeSemana;

    private Boolean disponibilidad; // Para activar/desactivar manualmente
}