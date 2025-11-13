package com.kane.librarymanagement.application.borrowing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtendDueDateRequest {

  @NotNull(message = "Additional days is required")
  @Min(value = 1, message = "Additional days must be at least 1")
  private Integer additionalDays;
}
