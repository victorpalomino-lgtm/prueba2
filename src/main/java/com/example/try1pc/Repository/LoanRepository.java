package com.example.try1pc.Repository;

import com.example.try1pc.Entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByUserIdAndStatusAndDueDateBefore(Long userId, String status, LocalDate date);

    Page<Loan> findByUserId(Long userId, Pageable pageable);
}
