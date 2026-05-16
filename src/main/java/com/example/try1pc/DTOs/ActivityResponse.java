package com.example.try1pc.DTOs;

import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class ActivityResponse {
    private String type; // "LOAN" o "RESERVATION"
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String status;

    // Campos específicos para préstamos (si type es "LOAN")
    private LocalDate borrowDate;
    private LocalDate dueDate;

    // Campos específicos para reservas (si type es "RESERVATION")
    private ZonedDateTime reservedAt;
    private ZonedDateTime expiresAt;
}
