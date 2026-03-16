package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlazaRequest {
    @NotNull(message = "El estacionamiento es obligatorio")
    private Integer estId;

    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;

    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private String tipo;

    private Boolean disponibilidad;
}