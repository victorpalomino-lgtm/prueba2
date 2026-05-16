package com.example.try1pc.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "El autor es obligatorio")
    private String author;

    private String isbn; // Opcional, por eso no lleva @NotBlank

    @NotNull(message = "El stock total es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo") // Regla del PDF
    private Integer totalCopies;
}
