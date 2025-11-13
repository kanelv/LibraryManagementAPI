package com.kane.librarymanagement.application.book.usecases;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.book.ISBN;
import com.kane.librarymanagement.application.book.dto.BookResponse;
import com.kane.librarymanagement.application.book.dto.CreateBookRequest;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateBookUseCase {

  private final BookRepository bookRepository;

  @Transactional
  public BookResponse execute(CreateBookRequest request) {

    ISBN isbn = ISBN.of(request.getIsbn());

    // Validate ISBN uniqueness
    if (bookRepository.existsByIsbn(isbn)) {
      throw new BusinessException("Book with ISBN " + request.getIsbn() + " already exists");
    }

    // Build domain entity
    Book book = Book.builder()
        .isbn(isbn)
        .title(request.getTitle())
        .author(request.getAuthor())
        .publisher(request.getPublisher())
        .publishedYear(request.getPublishedYear())
        .genre(request.getGenre())
        .totalCopies(request.getTotalCopies())
        .build();

    Book saved = bookRepository.save(book);

    // Map to response
    return BookResponse.builder()
        .id(saved.getId().value())
        .isbn(saved.getIsbn().value())
        .title(saved.getTitle())
        .author(saved.getAuthor())
        .publisher(saved.getPublisher())
        .publishedYear(saved.getPublishedYear())
        .genre(saved.getGenre())
        .totalCopies(saved.getTotalCopies())
        .availableCopies(saved.getAvailableCopies())
        .createdAt(saved.getCreatedAt())
        .build();
  }
}
