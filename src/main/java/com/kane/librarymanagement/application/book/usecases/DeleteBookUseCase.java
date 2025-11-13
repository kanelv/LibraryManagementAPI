package com.kane.librarymanagement.application.book.usecases;

import com.kane.librarymanagement.domain.book.Book;
import com.kane.librarymanagement.domain.book.BookId;
import com.kane.librarymanagement.domain.book.BookRepository;
import com.kane.librarymanagement.domain.shared.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteBookUseCase {
  private final BookRepository bookRepository;

  @Transactional
  public void execute(Long id) {
    BookId bookId = BookId.of(id);

    // Check if book exists
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new BusinessException("Book with ID " + id + " not found"));

    // Check if book has borrowed copies - uses domain business logic
    if (book.hasBorrowedCopies()) {
      throw new BusinessException("Cannot delete book with ID " + id + " because some copies are currently borrowed");
    }

    // Delete the book
    bookRepository.deleteById(bookId);
  }
}
