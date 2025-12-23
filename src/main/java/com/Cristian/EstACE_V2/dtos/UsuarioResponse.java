package com.Cristian.EstACE_V2.dtos;

import lombok.Data;

@Data
public class UsuarioResponse {
    private Integer legajo;
    private Integer dni;
    private String nombre;
    private String apellido;
    private String tipo;
    // NO incluimos el password
}
