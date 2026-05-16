package com.example.try1pc.Service;

import com.example.try1pc.DTOs.BookRequest;
import com.example.try1pc.DTOs.BookResponse;
import com.example.try1pc.DTOs.PagedResponse;
import com.example.try1pc.Entity.Book;
import com.example.try1pc.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookResponse createBook(BookRequest request) {
        // Regla del PDF: Validar que el ISBN sea único si se proporciona
        if (request.getIsbn() != null && !request.getIsbn().isBlank() && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("El ISBN ya está registrado en el sistema");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setTotalCopies(request.getTotalCopies());
        // El @PrePersist de tu entidad se encargará de igualar availableCopies a totalCopies

        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    public PagedResponse<BookResponse> getAllBooks(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        // Filtramos en memoria según el parámetro "status" del PDF (all, available, unavailable)
        List<BookResponse> filteredList = bookPage.getContent().stream()
                .filter(b -> {
                    if ("available".equalsIgnoreCase(status)) return b.getAvailableCopies() > 0;
                    if ("unavailable".equalsIgnoreCase(status)) return b.getAvailableCopies() == 0;
                    return true; // "all"
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                filteredList,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements()
        );
    }

    private BookResponse mapToResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setTotalCopies(book.getTotalCopies());
        response.setAvailableCopies(book.getAvailableCopies());
        return response;
    }
}
