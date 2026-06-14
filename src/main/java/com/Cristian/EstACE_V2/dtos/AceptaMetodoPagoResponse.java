package com.Cristian.EstACE_V2.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AceptaMetodoPagoResponse {
    private Integer estId;
    private String estNombre;
    private Integer metodoPagoId;
    private String metodoPagoDescripcion;
    private LocalDate desde;
    private LocalDate hasta;
}