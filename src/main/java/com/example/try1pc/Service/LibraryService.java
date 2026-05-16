package com.example.try1pc.Service;

import com.example.try1pc.DTOs.ActivityResponse;
import com.example.try1pc.DTOs.LoanRequest;
import com.example.try1pc.DTOs.PagedResponse;
import com.example.try1pc.Entity.Book;
import com.example.try1pc.Entity.Loan;
import com.example.try1pc.Entity.Reservation;
import com.example.try1pc.Exception.BookNotFoundException;
import com.example.try1pc.Exception.NoCopiesAvailableException;
import com.example.try1pc.Exception.OverdueLoanException;
import com.example.try1pc.Repository.BookRepository;
import com.example.try1pc.Repository.LoanRepository;
import com.example.try1pc.Repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;

    @Transactional // Si algo falla, hace rollback automático para no corromper el stock
    public Loan createLoan(LoanRequest request, Long userId) {
        // 1. Regla del PDF: Validar que el usuario no tenga préstamos activos ya VENCIDOS
        boolean hasOverdue = loanRepository.existsByUserIdAndStatusAndDueDateBefore(userId, "ACTIVE", LocalDate.now());
        if (hasOverdue) {
            throw new OverdueLoanException("El usuario no puede realizar nuevos préstamos porque tiene libros vencidos");
        }

        // 2. Buscar el libro en la BD
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("El libro solicitado no existe"));

        // 3. Regla del PDF: Validar si quedan copias disponibles
        if (book.getAvailableCopies() <= 0) {
            throw new NoCopiesAvailableException("No hay copias disponibles de este libro para préstamo");
        }

        // 4. Restar 1 al stock disponible y actualizar el libro
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 5. Crear el registro del préstamo (Las fechas automáticas las maneja el @PrePersist de la entidad)
        Loan loan = new Loan();
        loan.setBookId(book.getId());
        loan.setUserId(userId);
        if (request.getBorrowDate() != null) {
            loan.setBorrowDate(request.getBorrowDate());
        }

        return loanRepository.save(loan);
    }

    @Transactional
    public Reservation createReservation(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("El libro solicitado no existe"));

        // Regla del PDF: Solo se puede reservar si NO hay copias disponibles (availableCopies == 0)
        if (book.getAvailableCopies() > 0) {
            throw new IllegalArgumentException("No se puede reservar un libro que tiene copias disponibles para préstamo inmediato");
        }

        // Regla del PDF: El usuario no puede exceder el límite de 3 reservas activas (PENDING)
        long activeReservations = reservationRepository.countByUserIdAndStatus(userId, "PENDING");
        if (activeReservations >= 3) {
            throw new IllegalStateException("El usuario ya alcanzó el límite máximo de 3 reservas activas");
        }

        Reservation reservation = new Reservation();
        reservation.setBookId(book.getId());
        reservation.setUserId(userId);

        return reservationRepository.save(reservation);
    }

    public PagedResponse<ActivityResponse> getMyActivity(String type, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ActivityResponse> activities = new ArrayList<>();
        long totalElements = 0;

        // Caso 1: Si pide "loans" o pide "all", traemos los préstamos de la BD
        if ("loans".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type)) {
            Page<Loan> loans = loanRepository.findByUserId(userId, pageable);
            totalElements += loans.getTotalElements();
            for (Loan l : loans.getContent()) {
                Book b = bookRepository.findById(l.getBookId()).orElse(null);
                ActivityResponse act = new ActivityResponse();
                act.setType("LOAN");
                act.setId(l.getId());
                act.setBookId(l.getBookId());
                act.setBookTitle(b != null ? b.getTitle() : "Libro Desconocido");
                act.setStatus(l.getStatus());
                act.setBorrowDate(l.getBorrowDate());
                act.setDueDate(l.getDueDate());
                activities.add(act);
            }
        }

        // Caso 2: Si pide "reservations" o pide "all", traemos las reservas de la BD
        if ("reservations".equalsIgnoreCase(type) || "all".equalsIgnoreCase(type)) {
            Page<Reservation> reservations = reservationRepository.findByUserId(userId, pageable);
            totalElements += reservations.getTotalElements();
            for (Reservation r : reservations.getContent()) {
                Book b = bookRepository.findById(r.getBookId()).orElse(null);
                ActivityResponse act = new ActivityResponse();
                act.setType("RESERVATION");
                act.setId(r.getId());
                act.setBookId(r.getBookId());
                act.setBookTitle(b != null ? b.getTitle() : "Libro Desconocido");
                act.setStatus(r.getStatus());
                act.setReservedAt(r.getReservedAt());
                act.setExpiresAt(r.getExpiresAt());
                activities.add(act);
            }
        }

        return new PagedResponse<>(activities, page, size, totalElements);
    }
}
