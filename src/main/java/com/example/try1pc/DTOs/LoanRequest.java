package com.example.try1pc.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanRequest {
    @NotNull(message = "El id del libro es obligatorio")
    private Long bookId;

    private LocalDate borrowDate; // Puede ser opcional en el JSON; si es nulo usaremos hoy
}
