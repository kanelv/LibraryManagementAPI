package com.kane.librarymanagement.application.borrowing.usecases;

import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.borrowing.Borrowing;
import com.kane.librarymanagement.domain.borrowing.BorrowingId;
import com.kane.librarymanagement.domain.borrowing.BorrowingRepository;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for returning a borrowed book
 * Handles penalty calculation and book availability update
 */
@Service
@RequiredArgsConstructor
public class ReturnBookUseCase {

  private final BorrowingRepository borrowingRepository;
  private final BookRepository bookRepository;
  private final UserRepository userRepository;

  @Transactional
  public BorrowingResponse execute(Long borrowingId) {
    // Find borrowing
    Borrowing borrowing = borrowingRepository.findById(BorrowingId.of(borrowingId))
        .orElseThrow(() -> new BusinessException("Borrowing with ID " + borrowingId + " not found"));

    // Use domain logic to return book (automatically calculates penalty)
    borrowing.returnBook();

    // Increase book available copies using domain logic
    Book book = bookRepository.findById(borrowing.getBookId())
        .orElseThrow(() -> new BusinessException("Book with ID " + borrowing.getBookId().value() + " not found"));

    book.returnCopy();

    // Save changes
    bookRepository.save(book);
    Borrowing savedBorrowing = borrowingRepository.save(borrowing);

    // Get user info for response
    User user = userRepository.findById(borrowing.getUserId())
        .orElseThrow(() -> new BusinessException("User with ID " + borrowing.getUserId().value() + " not found"));

    // Map to response
    return BorrowingResponse.builder()
        .id(savedBorrowing.getId().value())
        .userId(savedBorrowing.getUserId().value())
        .username(user.getUsername())
        .bookId(savedBorrowing.getBookId().value())
        .bookTitle(book.getTitle())
        .bookIsbn(book.getIsbn().value())
        .borrowDate(savedBorrowing.getBorrowDate())
        .dueDate(savedBorrowing.getDueDate())
        .returnDate(savedBorrowing.getReturnDate())
        .status(savedBorrowing.getStatus())
        .penalty(savedBorrowing.getPenalty())
        .daysOverdue(savedBorrowing.getDaysOverdue())
        .isOverdue(savedBorrowing.isOverdue())
        .createdAt(savedBorrowing.getCreatedAt())
        .build();
  }
}
