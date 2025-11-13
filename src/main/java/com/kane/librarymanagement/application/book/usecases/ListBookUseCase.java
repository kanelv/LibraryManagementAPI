package com.kane.librarymanagement.application.book.usecases;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.application.book.dto.BookResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListBookUseCase {
  private final BookRepository bookRepository;

  @Transactional
  public List<BookResponse> execute() {
    List<Book> books = bookRepository.findAll();

    return books.stream().map(book ->
       BookResponse.builder()
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
           .build()
    ).toList();
  }
}
