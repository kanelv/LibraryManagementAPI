package com.kane.librarymanagement.interfaces.rest.controllers;

import com.kane.librarymanagement.application.borrowing.dto.BorrowBookRequest;
import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
import com.kane.librarymanagement.application.borrowing.dto.ExtendDueDateRequest;
import com.kane.librarymanagement.application.borrowing.usecases.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing book borrowing operations
 * Handles borrowing, returning, extending due dates, and querying borrowing records
 */
@RestController
@RequestMapping("/api/borrowings")
@RequiredArgsConstructor
public class BorrowingController {

  private final BorrowBookUseCase borrowBookUseCase;
  private final ReturnBookUseCase returnBookUseCase;
  private final GetUserBorrowingsUseCase getUserBorrowingsUseCase;
  private final GetBorrowingByIdUseCase getBorrowingByIdUseCase;
  private final ExtendDueDateUseCase extendDueDateUseCase;
  private final GetOverdueBorrowingsUseCase getOverdueBorrowingsUseCase;

  /**
   * Borrow a book
   * Creates a new borrowing record and decreases available book copies
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<BorrowingResponse> borrowBook(
      @Valid @RequestBody BorrowBookRequest request) {
    BorrowingResponse response = borrowBookUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Return a borrowed book
   * Marks the borrowing as returned, calculates penalties if overdue, and increases available copies
   */
  @PutMapping("/{id}/return")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<BorrowingResponse> returnBook(@PathVariable Long id) {
    BorrowingResponse response = returnBookUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Get all borrowings for a specific user
   * Returns both active and completed borrowings
   */
  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<List<BorrowingResponse>> getUserBorrowings(@PathVariable Long userId) {
    List<BorrowingResponse> response = getUserBorrowingsUseCase.execute(userId);
    return ResponseEntity.ok(response);
  }

  /**
   * Get detailed information about a specific borrowing
   */
  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<BorrowingResponse> getBorrowingById(@PathVariable Long id) {
    BorrowingResponse response = getBorrowingByIdUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Extend the due date of a borrowing
   * Validates that the book is still borrowed before extending
   */
  @PutMapping("/{id}/extend")
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  public ResponseEntity<BorrowingResponse> extendDueDate(
      @PathVariable Long id,
      @Valid @RequestBody ExtendDueDateRequest request) {
    BorrowingResponse response = extendDueDateUseCase.execute(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Get all overdue borrowings
   * Admin-only endpoint for monitoring overdue books and sending reminders
   */
  @GetMapping("/overdue")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<BorrowingResponse>> getOverdueBorrowings() {
    List<BorrowingResponse> response = getOverdueBorrowingsUseCase.execute();
    return ResponseEntity.ok(response);
  }
}
