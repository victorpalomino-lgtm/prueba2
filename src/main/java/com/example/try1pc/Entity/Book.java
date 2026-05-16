package com.example.try1pc.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(name = "books")
@Data
public class Book {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column( unique = true)
    private String isbn;

    @Min(value=0, message = "El stock total no puede ser negativo")
    @Column(nullable = false)
    private Integer totalCopies;

    @Column(nullable = false)
    private Integer availableCopies;

    @PrePersist
    public void initAvailableCopies() {
        if (this.availableCopies == null) {
            this.availableCopies = this.totalCopies;
        }
    }

}
