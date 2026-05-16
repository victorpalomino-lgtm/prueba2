package com.example.try1pc.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Genera constructor con parámetros para instanciarlo rápido
public class AuthResponse {
    private String token;
}
