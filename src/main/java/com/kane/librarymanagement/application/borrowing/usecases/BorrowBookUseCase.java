package com.kane.librarymanagement.application.borrowing.usecases;

import com.kane.librarymanagement.application.borrowing.dto.BorrowBookRequest;
import com.kane.librarymanagement.application.borrowing.dto.BorrowingResponse;
import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookId;
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

import java.util.Optional;

/**
 * Use case for borrowing a book
 * Validates user permissions, book availability, and existing borrowings
 */
@Service
@RequiredArgsConstructor
public class BorrowBookUseCase {

  private final BorrowingRepository borrowingRepository;
  private final UserRepository userRepository;
  private final BookRepository bookRepository;

  @Transactional
  public BorrowingResponse execute(BorrowBookRequest request) {
    UserId userId = UserId.of(request.getUserId());
    BookId bookId = BookId.of(request.getBookId());

    // Validate user exists and is active
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException("User with ID " + request.getUserId() + " not found"));

    if (!user.isActive()) {
      throw new BusinessException("User account is inactive. Cannot borrow books.");
    }

    // Validate book exists and is available
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new BusinessException("Book with ID " + request.getBookId() + " not found"));

    if (!book.isAvailable()) {
      throw new BusinessException("Book '" + book.getTitle() + "' is not available for borrowing");
    }

    // Check if user already has this book
    Optional<Borrowing> existingBorrowing = borrowingRepository
        .findActiveBorrowingByUserAndBook(userId, bookId);

    if (existingBorrowing.isPresent()) {
      throw new BusinessException("User has already borrowed this book");
    }

    // Check user's borrowing limit using domain logic
    int currentBorrowings = user.getBorrowedBooksCount();
    if (!user.getRole().canBorrow(currentBorrowings)) {
      throw new BusinessException(
          "User has reached maximum borrowing limit of " + user.getRole().maxBookNumber() + " books"
      );
    }

    // Create borrowing with domain logic
    Borrowing borrowing = Borrowing.builder()
        .userId(userId)
        .bookId(bookId)
        .build(); // Uses defaults: 14-day period, BORROWED status

    // Decrease book available copies using domain logic
    book.borrowCopy();

    // Save changes
    bookRepository.save(book);
    Borrowing savedBorrowing = borrowingRepository.save(borrowing);

    // Map to response
    return BorrowingResponse.builder()
        .id(savedBorrowing.getId().value())
        .userId(userId.value())
        .username(user.getUsername())
        .bookId(bookId.value())
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
