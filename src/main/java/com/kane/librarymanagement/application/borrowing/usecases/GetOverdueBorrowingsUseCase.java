package com.kane.librarymanagement.application.borrowing.usecases;

import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.borrowing.Borrowing;
import com.kane.librarymanagement.domain.borrowing.BorrowingRepository;
import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for getting all overdue borrowings
 * Typically used by admins or automated reminder systems
 */
@Service
@RequiredArgsConstructor
public class GetOverdueBorrowingsUseCase {

  private final BorrowingRepository borrowingRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  @Transactional(readOnly = true)
  public List<BorrowingResponse> execute() {
    // Get all overdue borrowings using domain repository
    List<Borrowing> overdueBorrowings = borrowingRepository.findOverdueBorrowings(LocalDate.now());

    // Map to response with user and book details
    return overdueBorrowings.stream()
        .map(borrowing -> {
          User user = userRepository.findById(borrowing.getUserId()).orElse(null);
          Book book = bookRepository.findById(borrowing.getBookId()).orElse(null);

          return BorrowingResponse.builder()
              .id(borrowing.getId().value())
              .userId(borrowing.getUserId().value())
              .username(user != null ? user.getUsername() : "Unknown")
              .bookId(borrowing.getBookId().value())
              .bookTitle(book != null ? book.getTitle() : "Unknown")
              .bookIsbn(book != null ? book.getIsbn().value() : "Unknown")
              .borrowDate(borrowing.getBorrowDate())
              .dueDate(borrowing.getDueDate())
              .returnDate(borrowing.getReturnDate())
              .status(borrowing.getStatus())
              .penalty(borrowing.calculateCurrentPenalty()) // Use domain logic for current penalty
              .daysOverdue(borrowing.getDaysOverdue())
              .isOverdue(borrowing.isOverdue())
              .createdAt(borrowing.getCreatedAt())
              .build();
        })
        .collect(Collectors.toList());
  }
}
