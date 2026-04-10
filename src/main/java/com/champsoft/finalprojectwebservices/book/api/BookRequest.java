package com.champsoft.finalprojectwebservices.book.api;

import com.champsoft.finalprojectwebservices.book.domain.BookStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank String title,
        @NotBlank String author,
        @NotNull BookStatus status,
        @Min(0) int availableCopies
) {
}
