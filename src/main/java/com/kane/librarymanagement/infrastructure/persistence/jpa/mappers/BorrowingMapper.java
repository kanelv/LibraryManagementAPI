package com.kane.librarymanagement.infrastructure.persistence.jpa.mappers;

import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.borrowing.Borrowing;
import com.kane.librarymanagement.domain.borrowing.BorrowingId;
import com.kane.librarymanagement.domain.user.UserId;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between JPA Borrowing entity and Domain Borrowing entity
 */
@Component
public class BorrowingMapper {

  /**
   * Converts JPA Borrowing entity to Domain Borrowing entity
   */
  public Borrowing toDomain(com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing jpaBorrowing) {
    if (jpaBorrowing == null) {
      return null;
    }

    return Borrowing.builder()
        .id(jpaBorrowing.getId() != null ? BorrowingId.of(jpaBorrowing.getId()) : null)
        .userId(jpaBorrowing.getUser() != null ? UserId.of(jpaBorrowing.getUser().getId()) : null)
        .bookId(jpaBorrowing.getBook() != null ? BookId.of(jpaBorrowing.getBook().getId()) : null)
        .borrowDate(jpaBorrowing.getBorrowDate())
        .dueDate(jpaBorrowing.getDueDate())
        .returnDate(jpaBorrowing.getReturnDate())
        .status(jpaBorrowing.getStatus())
        .penalty(jpaBorrowing.getPenalty())
        .createdAt(jpaBorrowing.getCreatedAt())
        .updatedAt(jpaBorrowing.getUpdatedAt())
        .build();
  }

  /**
   * Converts Domain Borrowing entity to JPA Borrowing entity
   * Note: User and Book entities must be fetched separately and set
   */
  public com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing toJpa(
      Borrowing domainBorrowing,
      com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User jpaUser,
      com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book jpaBook
  ) {
    if (domainBorrowing == null) {
      return null;
    }

    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing jpaBorrowing =
        new com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing();

    if (domainBorrowing.getId() != null) {
      jpaBorrowing.setId(domainBorrowing.getId().value());
    }
    jpaBorrowing.setUser(jpaUser);
    jpaBorrowing.setBook(jpaBook);
    jpaBorrowing.setBorrowDate(domainBorrowing.getBorrowDate());
    jpaBorrowing.setDueDate(domainBorrowing.getDueDate());
    jpaBorrowing.setReturnDate(domainBorrowing.getReturnDate());
    jpaBorrowing.setStatus(domainBorrowing.getStatus());
    jpaBorrowing.setPenalty(domainBorrowing.getPenalty());
    jpaBorrowing.setCreatedAt(domainBorrowing.getCreatedAt());
    jpaBorrowing.setUpdatedAt(domainBorrowing.getUpdatedAt());

    return jpaBorrowing;
  }

  /**
   * Updates JPA entity with values from Domain entity (for update operations)
   */
  public void updateJpaFromDomain(
      com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing jpaBorrowing,
      Borrowing domainBorrowing
  ) {
    if (jpaBorrowing == null || domainBorrowing == null) {
      return;
    }

    // Note: User and Book relationships are not updated here
    // Only the borrowing-specific fields
    jpaBorrowing.setBorrowDate(domainBorrowing.getBorrowDate());
    jpaBorrowing.setDueDate(domainBorrowing.getDueDate());
    jpaBorrowing.setReturnDate(domainBorrowing.getReturnDate());
    jpaBorrowing.setStatus(domainBorrowing.getStatus());
    jpaBorrowing.setPenalty(domainBorrowing.getPenalty());
  }
}
