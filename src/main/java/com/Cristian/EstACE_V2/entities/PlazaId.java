package com.Cristian.EstACE_V2.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlazaId implements Serializable {
    private Integer estId;
    private Integer plazaId;
}
