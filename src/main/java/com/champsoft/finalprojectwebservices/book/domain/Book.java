package com.champsoft.finalprojectwebservices.book.domain;

import com.champsoft.finalprojectwebservices.shared.domain.BusinessRuleViolationException;

import java.util.UUID;

public class Book {

    private final UUID id;
    private String title;
    private String author;
    private BookStatus status;
    private int availableCopies;

    public Book(UUID id, String title, String author, BookStatus status, int availableCopies) {
        this.id = id;
        this.title = requireText(title, "Book title is required");
        this.author = requireText(author, "Book author is required");
        this.status = status == null ? BookStatus.AVAILABLE : status;
        this.availableCopies = availableCopies;
        validateCopies(availableCopies);
    }

    public void update(String title, String author, BookStatus status, int availableCopies) {
        this.title = requireText(title, "Book title is required");
        this.author = requireText(author, "Book author is required");
        this.status = status == null ? BookStatus.AVAILABLE : status;
        this.availableCopies = availableCopies;
        validateCopies(availableCopies);
    }

    public void ensureAvailable(int quantity) {
        if (availableCopies < quantity) {
            throw new BusinessRuleViolationException("Cannot borrow book with insufficient available copies");
        }
    }

    public void borrowCopies(int quantity) {
        ensureAvailable(quantity);
        availableCopies -= quantity;
        status = availableCopies == 0 ? BookStatus.BORROWED : BookStatus.AVAILABLE;
    }

    public void returnCopies(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("Returned quantity must be positive");
        }
        availableCopies += quantity;
        status = BookStatus.AVAILABLE;
    }

    private void validateCopies(int copies) {
        if (copies < 0) {
            throw new BusinessRuleViolationException("Available copies cannot be negative");
        }
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException(message);
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }
}
