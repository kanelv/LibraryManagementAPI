package com.kane.librarymanagement.domain.book;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book domain entities
 * This works with domain entities, not JPA entities
 */
public interface BookRepository {
  Book save(Book book);
  Optional<Book> findById(BookId id);
  Optional<Book> findByIsbn(ISBN isbn);
  List<Book> findAll();
  void deleteById(BookId id);
  boolean existsByIsbn(ISBN isbn);
}
