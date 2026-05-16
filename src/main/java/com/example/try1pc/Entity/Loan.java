package com.example.try1pc.Entity;



import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Data
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String status = "ACTIVE";


    @PrePersist
    public void prePersistCalculations() {

        if (this.borrowDate == null) {
            this.borrowDate = LocalDate.now();
        }


        if (this.dueDate == null) {
            this.dueDate = this.borrowDate.plusDays(14);
        }
    }
}
