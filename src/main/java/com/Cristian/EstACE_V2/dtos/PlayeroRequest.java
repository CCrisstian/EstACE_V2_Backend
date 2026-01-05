package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PlayeroRequest {
    // Datos del Usuario
    @Min(value = 10000000, message = "El DNI debe tener 8 dígitos")
    @Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
    @NotNull(message = "El DNI es obligatorio")
    private Integer dni;

    // Regex: Solo letras, acentos y espacios
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
    private String nombre;

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
    private String apellido;

    @NotNull(message = "El password es obligatorio")
    private String password; // Solo obligatorio al crear

    // Datos del Playero
    @NotNull(message = "Debe seleccionar un Estacionamiento")
    private Integer estacionamientoId;

    private Boolean activo;
}