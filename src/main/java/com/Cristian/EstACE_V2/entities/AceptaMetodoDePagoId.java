package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AceptaMetodoDePagoId implements Serializable {

    @Column(name = "est_id")
    private Integer estId;

    @Column(name = "metodo_pago_id")
    private Integer metodoPagoId;

    // Hibernate requiere "estrictamente" equals() y hashCode() para claves compuestas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AceptaMetodoDePagoId that = (AceptaMetodoDePagoId) o;
        return Objects.equals(estId, that.estId) && Objects.equals(metodoPagoId, that.metodoPagoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estId, metodoPagoId);
    }
}