package com.kane.librarymanagement.infrastructure.persistence.jpa.repositories;

import com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Book persistence
 * Works with JPA entities only
 */
public interface BookJpaRepository extends JpaRepository<Book, Long> {
  Optional<Book> findByIsbn(String isbn);
  boolean existsByIsbn(String isbn);
}
