package com.Cristian.EstACE_V2.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlazaResponse {
    private Integer estId;
    private Integer plazaId;
    private Integer categoriaId;
    private String nombre;
    private String tipo;
    private Boolean disponibilidad;
}