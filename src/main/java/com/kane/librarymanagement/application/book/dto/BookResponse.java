package com.kane.librarymanagement.application.book.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
  private Long id;
  private String isbn;
  private String title;
  private String author;
  private String publisher;
  private Integer publishedYear;
  private String genre;
  private Integer totalCopies;
  private Integer availableCopies;
  private LocalDateTime createdAt;
}
