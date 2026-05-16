package com.example.try1pc.Repository;

import com.example.try1pc.Entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    long countByUserIdAndStatus(Long userId, String status);
    Page<Reservation> findByUserId(Long userId, Pageable pageable);
}
