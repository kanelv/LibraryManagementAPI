package com.kane.librarymanagement.interfaces.rest.controllers;

import com.kane.librarymanagement.application.book.usecases.*;
import com.kane.librarymanagement.application.book.dto.BookResponse;
import com.kane.librarymanagement.application.book.dto.CreateBookRequest;
import com.kane.librarymanagement.application.book.dto.UpdateBookRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

  private final CreateBookUseCase createBookUseCase;
  private final ListBookUseCase listBookUseCase;
  private final GetOneBookUseCase getOneBookUseCase;
  private final UpdateBookUseCase updateBookUseCase;
  private final DeleteBookUseCase deleteBookUseCase;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BookResponse> createBook(
      @Valid @RequestBody CreateBookRequest request) {
    BookResponse response = createBookUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<List<BookResponse>> listBooks() {
    List<BookResponse> response = listBookUseCase.execute();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{isbn}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
    BookResponse response = getOneBookUseCase.execute(isbn);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BookResponse> updateBook(
      @PathVariable Long id,
      @Valid @RequestBody UpdateBookRequest request) {
    BookResponse response = updateBookUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
    deleteBookUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }
}
