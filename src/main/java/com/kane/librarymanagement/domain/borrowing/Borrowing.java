package com.kane.librarymanagement.domain.borrowing;

import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.enums.BorrowStatus;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import com.kane.librarymanagement.domain.user.UserId;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Domain Entity representing a book borrowing transaction
 * Contains business logic for calculating penalties, due dates, and status management
 */
@Getter
public class Borrowing {
  private BorrowingId id;
  private UserId userId;
  private BookId bookId;
  private LocalDate borrowDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private BorrowStatus status;
  private BigDecimal penalty;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  // Business constants
  private static final BigDecimal PENALTY_PER_DAY = new BigDecimal("1.00"); // $1 per day late
  private static final int DEFAULT_BORROW_DURATION_DAYS = 14;

  private Borrowing() {
  }

  public static Builder builder() {
    return new Builder();
  }

  // Business logic methods

  /**
   * Check if the borrowing is currently overdue
   */
  public boolean isOverdue() {
    if (status == BorrowStatus.RETURNED) {
      return false;
    }
    return LocalDate.now().isAfter(dueDate);
  }

  /**
   * Calculate days overdue
   */
  public long getDaysOverdue() {
    if (!isOverdue()) {
      return 0;
    }
    return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
  }

  /**
   * Calculate current penalty amount
   */
  public BigDecimal calculateCurrentPenalty() {
    if (!isOverdue()) {
      return BigDecimal.ZERO;
    }
    long daysOverdue = getDaysOverdue();
    return PENALTY_PER_DAY.multiply(BigDecimal.valueOf(daysOverdue));
  }

  /**
   * Return the borrowed book
   */
  public void returnBook() {
    if (status == BorrowStatus.RETURNED) {
      throw new BusinessException("Book has already been returned");
    }

    this.returnDate = LocalDate.now();
    this.status = BorrowStatus.RETURNED;

    // Calculate final penalty if book was returned late
    if (returnDate.isAfter(dueDate)) {
      long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
      this.penalty = PENALTY_PER_DAY.multiply(BigDecimal.valueOf(daysLate));
    } else {
      this.penalty = BigDecimal.ZERO;
    }
  }

  /**
   * Update status to OVERDUE (should be called by a scheduled job)
   */
  public void markAsOverdue() {
    if (status == BorrowStatus.RETURNED) {
      throw new BusinessException("Cannot mark returned book as overdue");
    }
    if (!isOverdue()) {
      throw new BusinessException("Book is not yet overdue");
    }
    this.status = BorrowStatus.OVERDUE;
  }

  /**
   * Extend the due date
   */
  public void extendDueDate(int additionalDays) {
    if (status == BorrowStatus.RETURNED) {
      throw new BusinessException("Cannot extend due date for returned book");
    }
    if (additionalDays <= 0) {
      throw new BusinessException("Additional days must be positive");
    }
    this.dueDate = this.dueDate.plusDays(additionalDays);

    // Revert status from OVERDUE to BORROWED if extension brings it within due date
    if (status == BorrowStatus.OVERDUE && !isOverdue()) {
      this.status = BorrowStatus.BORROWED;
    }
  }

  /**
   * Check if penalty has been paid
   */
  public boolean hasPenalty() {
    return penalty != null && penalty.compareTo(BigDecimal.ZERO) > 0;
  }

  // Builder
  public static class Builder {
    private final Borrowing borrowing = new Borrowing();

    public Builder id(BorrowingId id) {
      borrowing.id = id;
      return this;
    }

    public Builder userId(UserId userId) {
      borrowing.userId = userId;
      return this;
    }

    public Builder bookId(BookId bookId) {
      borrowing.bookId = bookId;
      return this;
    }

    public Builder borrowDate(LocalDate borrowDate) {
      borrowing.borrowDate = borrowDate;
      return this;
    }

    public Builder dueDate(LocalDate dueDate) {
      borrowing.dueDate = dueDate;
      return this;
    }

    public Builder returnDate(LocalDate returnDate) {
      borrowing.returnDate = returnDate;
      return this;
    }

    public Builder status(BorrowStatus status) {
      borrowing.status = status;
      return this;
    }

    public Builder penalty(BigDecimal penalty) {
      borrowing.penalty = penalty;
      return this;
    }

    public Builder createdAt(OffsetDateTime createdAt) {
      borrowing.createdAt = createdAt;
      return this;
    }

    public Builder updatedAt(OffsetDateTime updatedAt) {
      borrowing.updatedAt = updatedAt;
      return this;
    }

    public Borrowing build() {
      if (borrowing.userId == null) {
        throw new BusinessException("User ID is required");
      }
      if (borrowing.bookId == null) {
        throw new BusinessException("Book ID is required");
      }
      if (borrowing.borrowDate == null) {
        borrowing.borrowDate = LocalDate.now();
      }
      if (borrowing.dueDate == null) {
        borrowing.dueDate = borrowing.borrowDate.plusDays(DEFAULT_BORROW_DURATION_DAYS);
      }
      if (borrowing.status == null) {
        borrowing.status = BorrowStatus.BORROWED;
      }
      if (borrowing.penalty == null) {
        borrowing.penalty = BigDecimal.ZERO;
      }
      return borrowing;
    }
  }
}
