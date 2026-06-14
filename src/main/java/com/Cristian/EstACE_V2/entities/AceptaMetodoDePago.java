package com.Cristian.EstACE_V2.entities;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;

@Entity
@Table(name = "acepta_metodo_de_pago", schema = "public")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AceptaMetodoDePago {

    @EmbeddedId
    private AceptaMetodoDePagoId id = new AceptaMetodoDePagoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("estId") // Le dice a Hibernate que este campo completa el 'estId' de la clave compuesta
    @JoinColumn(name = "est_id")
    @JsonIgnore // Evita bucles infinitos al convertir a JSON
    private Estacionamiento estacionamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("metodoPagoId") // Completa el 'metodoPagoId' de la clave compuesta
    @JoinColumn(name = "metodo_pago_id")
    private MetodoDePago metodoDePago;

    @Column(name = "amp_desde")
    private LocalDate desde;

    @Column(name = "amp_hasta")
    private LocalDate hasta;
}