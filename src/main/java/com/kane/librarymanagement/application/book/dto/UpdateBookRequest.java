package com.kane.librarymanagement.application.book.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookRequest {

  @NotBlank(message = "Title is required")
  @Size(max = 200)
  private String title;

  @NotBlank(message = "Author is required")
  @Size(max = 100)
  private String author;

  @Size(max = 100)
  private String publisher;

  @Min(1440) // Earliest reasonable year
  @Max(2100)
  private Integer publishedYear;

  @Size(max = 50)
  private String genre;

  @Min(1)
  private Integer totalCopies;
}
