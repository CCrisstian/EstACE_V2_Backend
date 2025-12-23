package com.Cristian.EstACE_V2.dtos;

import lombok.Data;

@Data // Lombok genera Getters y Setters automáticamente
public class LoginRequest {
    private Integer legajo;
    private String password;
}
