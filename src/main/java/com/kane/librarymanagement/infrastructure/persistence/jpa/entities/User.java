package com.kane.librarymanagement.infrastructure.persistence.jpa.entities;

import com.kane.librarymanagement.domain.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"borrowedBooks"})
@Entity
@Table(
    name="\"user\"",  // Quoted to handle reserved keyword in H2
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_phoneNumber", columnList = "phoneNumber"),
        @Index(name = "idx_email", columnList = "email")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uc_username_phoneNumber", columnNames = {"username", "phoneNumber"})
    }
)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
  @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
  private Long id;

  @Column(unique=true, nullable = false, length = 100)
  private String username;

  @Column(nullable = false, length = 15)
  private String phoneNumber;

  @Column(unique=true, nullable = false, length = 50)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role_type", nullable = false)
  private RoleType roleType;

  @Column(name = "max_book_number")
  private Integer maxBookNumber;

  @Column(name = "max_borrow_duration")
  private Integer maxBorrowDuration;

  @Column(nullable = false)
  private String password;

  @Column(length = 50)
  private String firstName;

  @Column(length = 50)
  private String lastName;

  @Column()
  private String address;

  @Column(nullable = false)
  private Boolean active = true;

  @Column(nullable = false)
  private LocalDate membershipDate = LocalDate.now();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private OffsetDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  // Many-to-many relationship with custom join table
  @ManyToMany
  @JoinTable(
      name = "borrowing_histories",  // Custom join table name
      joinColumns = @JoinColumn(name = "user_id"),  // Custom foreign key for User
      inverseJoinColumns = @JoinColumn(name = "book_id")  // Custom foreign key for Book
  )
  private List<Book> borrowedBooks = new ArrayList<>();
}
