package com.kane.librarymanagement.application.borrowing.usecases;

import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.borrowing.Borrowing;
import com.kane.librarymanagement.domain.borrowing.BorrowingRepository;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import com.kane.librarymanagement.domain.user.User;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for getting all borrowings for a specific user
 * Returns both active and completed borrowings
 */
@Service
@RequiredArgsConstructor
public class GetUserBorrowingsUseCase {

  private final BorrowingRepository borrowingRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  @Transactional(readOnly = true)
  public List<BorrowingResponse> execute(Long userId) {
    UserId userIdVO = UserId.of(userId);

    // Validate user exists
    User user = userRepository.findById(userIdVO)
        .orElseThrow(() -> new BusinessException("User with ID " + userId + " not found"));

    // Get all borrowings for user
    List<Borrowing> borrowings = borrowingRepository.findByUserId(userIdVO);

    // Map to response with book details
    return borrowings.stream()
        .map(borrowing -> {
          Book book = bookRepository.findById(borrowing.getBookId())
              .orElse(null); // Handle case where book might be deleted

          return BorrowingResponse.builder()
              .id(borrowing.getId().value())
              .userId(userIdVO.value())
              .username(user.getUsername())
              .bookId(borrowing.getBookId().value())
              .bookTitle(book != null ? book.getTitle() : "Unknown")
              .bookIsbn(book != null ? book.getIsbn().value() : "Unknown")
              .borrowDate(borrowing.getBorrowDate())
              .dueDate(borrowing.getDueDate())
              .returnDate(borrowing.getReturnDate())
              .status(borrowing.getStatus())
              .penalty(borrowing.getPenalty())
              .daysOverdue(borrowing.getDaysOverdue())
              .isOverdue(borrowing.isOverdue())
              .createdAt(borrowing.getCreatedAt())
              .build();
        })
        .collect(Collectors.toList());
  }
}
