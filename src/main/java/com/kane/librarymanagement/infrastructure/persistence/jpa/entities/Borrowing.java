package com.kane.librarymanagement.infrastructure.persistence.jpa.entities;

import com.kane.librarymanagement.domain.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;


@Getter
@Setter
@Entity
@Table(name = "borrowing_history")  // Custom join table
public class Borrowing {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "borrowing_history_seq")
  @SequenceGenerator(name = "borrowing_history_seq", sequenceName = "borrowing_history_seq", allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")  // Custom foreign key for User
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")  // Custom foreign key for Book
  private Book book;

  @Column(nullable = false)
  private LocalDate borrowDate = LocalDate.now();

  @Column(nullable = false)
  private LocalDate dueDate = this.borrowDate.plusDays(14);

  @Column()
  private LocalDate returnDate;

  /**
   * Store the enum as a VARCHAR in DB
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BorrowStatus status = BorrowStatus.BORROWED;

  @Column(nullable = false)
  private BigDecimal penalty = BigDecimal.ZERO;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  // for JPA only, no use
  public Borrowing() {}

  public Borrowing(User user, Book book) {
    this.user = user;
    this.book = book;
    this.borrowDate = LocalDate.now();
    this.dueDate = this.borrowDate.plusDays(14);
    this.status = BorrowStatus.BORROWED;
    this.penalty = BigDecimal.ZERO;
  }
}
