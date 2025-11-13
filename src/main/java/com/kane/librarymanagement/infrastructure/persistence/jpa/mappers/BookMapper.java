package com.kane.librarymanagement.infrastructure.persistence.jpa.mappers;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.book.ISBN;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between JPA Book entity and Domain Book entity
 */
@Component
public class BookMapper {

  /**
   * Converts JPA Book entity to Domain Book entity
   */
  public Book toDomain(com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book jpaBook) {
    if (jpaBook == null) {
      return null;
    }

    return Book.builder()
        .id(jpaBook.getId() != null ? BookId.of(jpaBook.getId()) : null)
        .isbn(ISBN.of(jpaBook.getIsbn()))
        .title(jpaBook.getTitle())
        .author(jpaBook.getAuthor())
        .publisher(jpaBook.getPublisher())
        .publishedYear(jpaBook.getPublishedYear())
        .genre(jpaBook.getGenre())
        .price(jpaBook.getPrice())
        .totalCopies(jpaBook.getTotalCopies())
        .availableCopies(jpaBook.getAvailableCopies())
        .createdAt(jpaBook.getCreatedAt())
        .updatedAt(jpaBook.getUpdatedAt())
        .build();
  }

  /**
   * Converts Domain Book entity to JPA Book entity
   */
  public com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book toJpa(Book domainBook) {
    if (domainBook == null) {
      return null;
    }

    return com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book.builder()
        .id(domainBook.getId() != null ? domainBook.getId().value() : null)
        .isbn(domainBook.getIsbn().value())
        .title(domainBook.getTitle())
        .author(domainBook.getAuthor())
        .publisher(domainBook.getPublisher())
        .publishedYear(domainBook.getPublishedYear())
        .genre(domainBook.getGenre())
        .price(domainBook.getPrice())
        .totalCopies(domainBook.getTotalCopies())
        .availableCopies(domainBook.getAvailableCopies())
        .createdAt(domainBook.getCreatedAt())
        .updatedAt(domainBook.getUpdatedAt())
        .build();
  }

  /**
   * Updates JPA entity with values from Domain entity (for update operations)
   */
  public void updateJpaFromDomain(com.kane.librarymanagement.infrastructure.persistence.jpa.entities.Book jpaBook, Book domainBook) {
    if (jpaBook == null || domainBook == null) {
      return;
    }

    jpaBook.setIsbn(domainBook.getIsbn().value());
    jpaBook.setTitle(domainBook.getTitle());
    jpaBook.setAuthor(domainBook.getAuthor());
    jpaBook.setPublisher(domainBook.getPublisher());
    jpaBook.setPublishedYear(domainBook.getPublishedYear());
    jpaBook.setGenre(domainBook.getGenre());
    jpaBook.setPrice(domainBook.getPrice());
    jpaBook.setTotalCopies(domainBook.getTotalCopies());
    jpaBook.setAvailableCopies(domainBook.getAvailableCopies());
  }
}
