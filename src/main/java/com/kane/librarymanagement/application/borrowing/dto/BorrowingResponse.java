package com.kane.librarymanagement.application.borrowing.dto;

import com.kane.librarymanagement.domain.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingResponse {
  private Long id;
  private Long userId;
  private String username;
  private Long bookId;
  private String bookTitle;
  private String bookIsbn;
  private LocalDate borrowDate;
  private LocalDate dueDate;
  private LocalDate returnDate;
  private BorrowStatus status;
  private BigDecimal penalty;
  private Long daysOverdue;
  private boolean isOverdue;
  private OffsetDateTime createdAt;
}
