package com.kane.librarymanagement.infrastructure.persistence.repositories;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.book.ISBN;
import com.kane.librarymanagement.infrastructure.persistence.jpa.mappers.BookMapper;
import com.kane.librarymanagement.infrastructure.persistence.jpa.repositories.BookJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BookRepository that bridges domain layer and persistence layer
 * This is an adapter that implements the domain repository interface
 * Uses JPA repository for persistence and mapper for conversions
 */
@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

  private final BookJpaRepository jpaRepository;
  private final BookMapper mapper;

  @Override
  public Book save(Book book) {
    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book jpaBook;

    if (book.getId() != null) {
      // Update existing book
      jpaBook = jpaRepository.findById(book.getId().value())
          .orElseThrow(() -> new RuntimeException("Book not found with id: " + book.getId().value()));
      mapper.updateJpaFromDomain(jpaBook, book);
    } else {
      // Create new book
      jpaBook = mapper.toJpa(book);
    }

    com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book savedJpaBook = jpaRepository.save(jpaBook);
    return mapper.toDomain(savedJpaBook);
  }

  @Override
  public Optional<Book> findById(BookId id) {
    return jpaRepository.findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Book> findByIsbn(ISBN isbn) {
    return jpaRepository.findByIsbn(isbn.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Book> findAll() {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(BookId id) {
    jpaRepository.deleteById(id.value());
  }

  @Override
  public boolean existsByIsbn(ISBN isbn) {
    return jpaRepository.existsByIsbn(isbn.value());
  }
}
