package com.example.try1pc.Controller;



import com.example.try1pc.Entity.Loan;
import com.example.try1pc.Entity.Reservation;
import com.example.try1pc.DTOs.ActivityResponse;
import com.example.try1pc.DTOs.LoanRequest;
import com.example.try1pc.DTOs.PagedResponse;
import com.example.try1pc.Service.LibraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @PostMapping("/loans")
    public ResponseEntity<Loan> createLoan(
            @Valid @RequestBody LoanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        // En un entorno real, buscarías el ID real del usuario usando su username: userDetails.getUsername()
        // Para asegurar velocidad en el examen, usamos un ID simulado (1L)
        Long mockUserId = 1L;

        Loan loan = libraryService.createLoan(request, mockUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    @PostMapping("/books/{bookId}/reserve")
    public ResponseEntity<Reservation> createReservation(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long mockUserId = 1L;
        Reservation reservation = libraryService.createReservation(bookId, mockUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping("/my-activity")
    public ResponseEntity<PagedResponse<ActivityResponse>> getMyActivity(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long mockUserId = 1L;
        PagedResponse<ActivityResponse> response = libraryService.getMyActivity(type, mockUserId, page, size);
        return ResponseEntity.ok(response);
    }
}
