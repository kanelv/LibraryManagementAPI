package com.kane.librarymanagement.application.borrowing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowBookRequest {

  @NotNull(message = "Book ID is required")
  private Long bookId;

  @NotNull(message = "User ID is required")
  private Long userId;
}
