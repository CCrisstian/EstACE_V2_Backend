package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {
    // Datos comunes (Dueño y Playero)

    @Min(value = 10000000, message = "El DNI debe tener 8 dígitos")
    @Max(value = 99999999, message = "El DNI no puede tener más de 8 dígitos")
    private Integer dni;

    // Regex: Solo letras, acentos y espacios
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras")
    private String nombre;

    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras")
    private String apellido;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^(?:(?:00|\\+)?54\\s?9?\\s?)?(?:11|[234678]\\d{2,3})[\\s-]?[0-9]{6,8}$",
            message = "Formato de teléfono argentino inválido. Ej: +54 9 11 1234-5678 o 3704 123456")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    // Dato exclusivo (solo Dueño puede editar su propia contraseña)
    private String password;

    private String avatarUrl;
}