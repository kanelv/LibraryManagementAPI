package com.kane.librarymanagement.application.book.usecases;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.application.book.dto.BookResponse;
import com.kane.librarymanagement.application.book.dto.UpdateBookRequest;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateBookUseCase {
  private final BookRepository bookRepository;

  @Transactional
  public BookResponse execute(Long id, UpdateBookRequest request) {
    // Find existing book
    Book book = bookRepository.findById(BookId.of(id))
        .orElseThrow(() -> new BusinessException("Book with ID " + id + " not found"));

    // Use domain business logic to update details
    book.updateDetails(
        request.getTitle(),
        request.getAuthor(),
        request.getPublisher(),
        request.getPublishedYear(),
        request.getGenre()
    );

    // Update total copies if provided - uses domain business logic
    if (request.getTotalCopies() != null) {
      book.updateTotalCopies(request.getTotalCopies());
    }

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
