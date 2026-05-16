package com.example.try1pc.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El identificador (username o email) es obligatorio")
    private String identifier; // El PDF permite loguearse con cualquiera de los dos

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}