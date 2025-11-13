package com.kane.librarymanagement.application.borrowing.usecases;

import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
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
 * Use case for getting detailed information about a specific borrowing
 */
@Service
@RequiredArgsConstructor
public class GetBorrowingByIdUseCase {

  private final BorrowingRepository borrowingRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  @Transactional(readOnly = true)
  public BorrowingResponse execute(Long borrowingId) {
    // Find borrowing
    Borrowing borrowing = borrowingRepository.findById(BorrowingId.of(borrowingId))
        .orElseThrow(() -> new BusinessException("Borrowing with ID " + borrowingId + " not found"));

    // Get user and book details
    User user = userRepository.findById(borrowing.getUserId())
        .orElseThrow(() -> new BusinessException("User with ID " + borrowing.getUserId().value() + " not found"));

    Book book = bookRepository.findById(borrowing.getBookId())
        .orElseThrow(() -> new BusinessException("Book with ID " + borrowing.getBookId().value() + " not found"));

    // Map to response
    return BorrowingResponse.builder()
        .id(borrowing.getId().value())
        .userId(borrowing.getUserId().value())
        .username(user.getUsername())
        .bookId(borrowing.getBookId().value())
        .bookTitle(book.getTitle())
        .bookIsbn(book.getIsbn().value())
        .borrowDate(borrowing.getBorrowDate())
        .dueDate(borrowing.getDueDate())
        .returnDate(borrowing.getReturnDate())
        .status(borrowing.getStatus())
        .penalty(borrowing.getPenalty())
        .daysOverdue(borrowing.getDaysOverdue())
        .isOverdue(borrowing.isOverdue())
        .createdAt(borrowing.getCreatedAt())
        .build();
  }
}
