package com.kane.librarymanagement.application.book.usecases;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.book.ISBN;
import com.kane.librarymanagement.application.book.dto.BookResponse;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOneBookUseCase {
  private final BookRepository bookRepository;

  @Transactional
  public BookResponse execute(String isbn) {
    Book book = bookRepository.findByIsbn(ISBN.of(isbn))
        .orElseThrow(() -> new BusinessException("Book with ISBN " + isbn + " not found"));

    return BookResponse.builder()
        .id(book.getId().value())
        .isbn(book.getIsbn().value())
        .title(book.getTitle())
        .author(book.getAuthor())
        .publisher(book.getPublisher())
        .publishedYear(book.getPublishedYear())
        .genre(book.getGenre())
        .totalCopies(book.getTotalCopies())
        .availableCopies(book.getAvailableCopies())
        .createdAt(book.getCreatedAt())
        .build();
  }
}
