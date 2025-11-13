package com.kane.librarymanagement.infrastructure.persistence.jpa.repositories;

import com.kane.librarymanagement.domain.enums.BorrowStatus;
import com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Borrowing persistence
 * Works with JPA entities only
 */
public interface BorrowingJpaRepository extends JpaRepository<Borrowing, Long> {

  /**
   * Find all borrowings for a specific user
   */
  @Query("SELECT b FROM Borrowing b WHERE b.user.id = :userId")
  List<Borrowing> findByUserId(@Param("userId") Long userId);

  /**
   * Find all borrowings for a specific book
   */
  @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId")
  List<Borrowing> findByBookId(@Param("bookId") Long bookId);

  /**
   * Find active borrowings for a user (not returned)
   */
  @Query("SELECT b FROM Borrowing b WHERE b.user.id = :userId AND b.status != 'RETURNED'")
  List<Borrowing> findActiveBorrowingsByUserId(@Param("userId") Long userId);

  /**
   * Find active borrowings for a book (not returned)
   */
  @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.status != 'RETURNED'")
  List<Borrowing> findActiveBorrowingsByBookId(@Param("bookId") Long bookId);

  /**
   * Find all borrowings with a specific status
   */
  List<Borrowing> findByStatus(BorrowStatus status);

  /**
   * Find all overdue borrowings (due date passed and not returned)
   */
  @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :currentDate AND b.status != 'RETURNED'")
  List<Borrowing> findOverdueBorrowings(@Param("currentDate") LocalDate currentDate);

  /**
   * Check if a specific user has an active borrowing for a specific book
   */
  @Query("SELECT b FROM Borrowing b WHERE b.user.id = :userId AND b.book.id = :bookId AND b.status != 'RETURNED'")
  Optional<Borrowing> findActiveBorrowingByUserAndBook(
      @Param("userId") Long userId,
      @Param("bookId") Long bookId
  );

  /**
   * Find borrowings due within a certain number of days
   */
  @Query("SELECT b FROM Borrowing b WHERE b.dueDate BETWEEN :startDate AND :endDate AND b.status = 'BORROWED'")
  List<Borrowing> findBorrowingsDueWithinPeriod(
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );
}
