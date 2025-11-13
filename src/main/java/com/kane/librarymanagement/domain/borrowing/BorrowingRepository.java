package com.kane.librarymanagement.domain.borrowing;

import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.enums.BorrowStatus;
import com.kane.librarymanagement.domain.user.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Borrowing domain entities
 * This works with domain entities, not JPA entities
 */
public interface BorrowingRepository {

  Borrowing save(Borrowing borrowing);

  Optional<Borrowing> findById(BorrowingId id);

  List<Borrowing> findAll();

  void deleteById(BorrowingId id);

  /**
   * Find all borrowings for a specific user
   */
  List<Borrowing> findByUserId(UserId userId);

  /**
   * Find all borrowings for a specific book
   */
  List<Borrowing> findByBookId(BookId bookId);

  /**
   * Find active borrowings for a user (not returned)
   */
  List<Borrowing> findActiveBorrowingsByUserId(UserId userId);

  /**
   * Find active borrowings for a book (not returned)
   */
  List<Borrowing> findActiveBorrowingsByBookId(BookId bookId);

  /**
   * Find all borrowings with a specific status
   */
  List<Borrowing> findByStatus(BorrowStatus status);

  /**
   * Find all overdue borrowings (due date passed and not returned)
   */
  List<Borrowing> findOverdueBorrowings(LocalDate currentDate);

  /**
   * Check if a specific user has an active borrowing for a specific book
   */
  Optional<Borrowing> findActiveBorrowingByUserAndBook(UserId userId, BookId bookId);

  /**
   * Find borrowings due within a certain number of days
   */
  List<Borrowing> findBorrowingsDueWithinPeriod(LocalDate startDate, LocalDate endDate);
}
