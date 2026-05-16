package com.example.try1pc.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId; // Referencia simple al libro [cite: 26]

    @Column(name = "user_id", nullable = false)
    private Long userId; // Referencia simple al usuario [cite: 27]

    @Column(name = "reserved_at", nullable = false)
    private ZonedDateTime reservedAt; //

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt; // [cite: 29]

    @Column(nullable = false)
    private String status = "PENDING"; // El PDF indica los estados: PENDING, EXPIRED, CANCELLED [cite: 29]

    // =======================================================
    // Lógica auto-calculada justa antes de guardar en Postgres
    // =======================================================
    @PrePersist
    public void prePersistCalculations() {
        // 1. Si no viene una fecha de reserva preestablecida, se asigna la actual
        if (this.reservedAt == null) {
            this.reservedAt = ZonedDateTime.now();
        }

        // 2. Regla estricta del PDF: expira exactamente en 48 horas [cite: 29]
        if (this.expiresAt == null) {
            this.expiresAt = this.reservedAt.plusHours(48); // [cite: 29]
        }
    }



}
