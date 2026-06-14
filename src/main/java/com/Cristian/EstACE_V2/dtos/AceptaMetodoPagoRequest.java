package com.Cristian.EstACE_V2.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AceptaMetodoPagoRequest {
    @NotNull(message = "El estacionamiento es obligatorio")
    private Integer estId;

    @NotNull(message = "El método de pago es obligatorio")
    private Integer metodoPagoId;

    private LocalDate desde;
    private LocalDate hasta;
}