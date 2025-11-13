package com.kane.librarymanagement.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"users"})
@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
  @SequenceGenerator(name = "book_seq", sequenceName = "book_seq", allocationSize = 1)
  private Long id;

  @Column(unique = true, nullable = false, length = 20)
  private String isbn;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, length = 100)
  private String author;

  @Column(length = 100)
  private String publisher;

  private Integer publishedYear;

  @Column(length = 50)
  private String genre;

  @Column(precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "total_copies", nullable = false)
  private Integer totalCopies = 0;

  @Column(name = "available_copies", nullable = false)
  private Integer availableCopies = 0;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @ManyToMany(mappedBy = "borrowedBooks")
  private List<User> users = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (this.availableCopies == null) {
      availableCopies = totalCopies;
    }
  }
}
