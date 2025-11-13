package com.kane.librarymanagement.infrastructure.persistence.repositories;

import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.borrowing.Borrowing;
import com.kane.librarymanagement.domain.borrowing.BorrowingId;
import com.kane.librarymanagement.domain.borrowing.BorrowingRepository;
import com.kane.librarymanagement.domain.enums.BorrowStatus;
import com.kane.librarymanagement.domain.user.UserId;
import com.kane.librarymanagement.infrastructure.persistence.jpa.mappers.BorrowingMapper;
import com.kane.librarymanagement.infrastructure.persistence.jpa.repositories.BorrowingJpaRepository;
import com.kane.librarymanagement.infrastructure.persistence.jpa.repositories.BookJpaRepository;
import com.kane.librarymanagement.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BorrowingRepository that bridges domain layer and persistence layer
 * This is an adapter that implements the domain repository interface
 * Uses JPA repository for persistence and mapper for conversions
 */
@Repository
@RequiredArgsConstructor
public class BorrowingRepositoryImpl implements BorrowingRepository {

  private final BorrowingJpaRepository jpaRepository;
  private final BorrowingMapper mapper;
  private final UserJpaRepository userJpaRepository;
  private final BookJpaRepository bookJpaRepository;

  @Override
  public Borrowing save(Borrowing borrowing) {
    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing jpaBorrowing;

    // Fetch User and Book JPA entities
    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.User jpaUser =
        userJpaRepository.findById(borrowing.getUserId().value())
            .orElseThrow(() -> new RuntimeException("User not found with id: " + borrowing.getUserId().value()));

    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book jpaBook =
        bookJpaRepository.findById(borrowing.getBookId().value())
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + borrowing.getBookId().value()));

    if (borrowing.getId() != null) {
      // Update existing borrowing
      jpaBorrowing = jpaRepository.findById(borrowing.getId().value())
          .orElseThrow(() -> new RuntimeException("Borrowing not found with id: " + borrowing.getId().value()));
      mapper.updateJpaFromDomain(jpaBorrowing, borrowing);
    } else {
      // Create new borrowing
      jpaBorrowing = mapper.toJpa(borrowing, jpaUser, jpaBook);
    }

    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Borrowing savedJpaBorrowing =
        jpaRepository.save(jpaBorrowing);
    return mapper.toDomain(savedJpaBorrowing);
  }

  @Override
  public Optional<Borrowing> findById(BorrowingId id) {
    return jpaRepository.findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Borrowing> findAll() {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(BorrowingId id) {
    jpaRepository.deleteById(id.value());
  }

  @Override
  public List<Borrowing> findByUserId(UserId userId) {
    return jpaRepository.findByUserId(userId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Borrowing> findByBookId(BookId bookId) {
    return jpaRepository.findByBookId(bookId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Borrowing> findActiveBorrowingsByUserId(UserId userId) {
    return jpaRepository.findActiveBorrowingsByUserId(userId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Borrowing> findActiveBorrowingsByBookId(BookId bookId) {
    return jpaRepository.findActiveBorrowingsByBookId(bookId.value()).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Borrowing> findByStatus(BorrowStatus status) {
    return jpaRepository.findByStatus(status).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Borrowing> findOverdueBorrowings(LocalDate currentDate) {
    return jpaRepository.findOverdueBorrowings(currentDate).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Borrowing> findActiveBorrowingByUserAndBook(UserId userId, BookId bookId) {
    return jpaRepository.findActiveBorrowingByUserAndBook(userId.value(), bookId.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Borrowing> findBorrowingsDueWithinPeriod(LocalDate startDate, LocalDate endDate) {
    return jpaRepository.findBorrowingsDueWithinPeriod(startDate, endDate).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }
}

