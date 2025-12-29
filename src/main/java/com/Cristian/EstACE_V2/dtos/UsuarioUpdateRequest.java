package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {
    // Datos comunes (Dueño y Playero)

    @Min(value = 10000000, message = "El DNI debe tener 8 dígitos")
    @Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
    private Integer dni;

    private String nombre;
    private String apellido;

    // Dato exclusivo (solo Dueño puede editar su propia contraseña)
    private String password;
}