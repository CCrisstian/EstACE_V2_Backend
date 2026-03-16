package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlazaLoteRequest {
    @NotNull(message = "El estacionamiento es obligatorio")
    private Integer estId;

    @NotNull(message = "La categoría es obligatoria")
    private Integer categoriaId;

    @NotNull(message = "El tipo es obligatorio")
    private String tipo;

    private Boolean disponibilidad;

    @NotEmpty(message = "Debe enviar al menos un nombre para crear las plazas")
    private List<String> nombres;
}