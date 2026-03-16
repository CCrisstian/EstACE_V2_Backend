package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsuarioResponse {
    private Integer legajo;
    private Integer dni;
    private String nombre;
    private String apellido;
    private String tipo;
    private String token;
    private String avatarUrl;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "El teléfono debe contener entre 8 y 15 números. Puede iniciar con '+'")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
}
