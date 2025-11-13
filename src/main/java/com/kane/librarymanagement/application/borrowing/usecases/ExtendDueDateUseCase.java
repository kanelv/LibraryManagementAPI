package com.kane.librarymanagement.application.borrowing.usecases;

import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
import com.kane.librarymanagement.application.borrowing.dto.ExtendDueDateRequest;
import com.kane.librarymanagement.domain.book.Book;
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
 * Use case for extending the due date of a borrowing
 * Validates that the book is still borrowed before extending
 */
@Service
@RequiredArgsConstructor
public class ExtendDueDateUseCase {

  private final BorrowingRepository borrowingRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  @Transactional
  public BorrowingResponse execute(Long borrowingId, ExtendDueDateRequest request) {
    // Find borrowing
    Borrowing borrowing = borrowingRepository.findById(BorrowingId.of(borrowingId))
        .orElseThrow(() -> new BusinessException("Borrowing with ID " + borrowingId + " not found"));

    // Use domain logic to extend due date (includes validation)
    borrowing.extendDueDate(request.getAdditionalDays());

    // Save changes
    Borrowing savedBorrowing = borrowingRepository.save(borrowing);

    // Get user and book info for response
    User user = userRepository.findById(borrowing.getUserId())
        .orElseThrow(() -> new BusinessException("User with ID " + borrowing.getUserId().value() + " not found"));

    Book book = bookRepository.findById(borrowing.getBookId())
        .orElseThrow(() -> new BusinessException("Book with ID " + borrowing.getBookId().value() + " not found"));

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
