package com.champsoft.catalogservice.catalog.application;

import com.champsoft.catalogservice.catalog.dataaccess.BookEntity;
import com.champsoft.catalogservice.catalog.dataaccess.BookRepository;
import com.champsoft.catalogservice.catalog.domain.BookResponse;
import com.champsoft.catalogservice.catalog.domain.BookStatus;
import com.champsoft.catalogservice.catalog.domain.DuplicateBookTitleException;
import com.champsoft.catalogservice.catalog.domain.InvalidBookStateException;
import com.champsoft.catalogservice.catalog.domain.UpsertBookRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CatalogService {

    private final BookRepository bookRepository;

    public CatalogService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(UUID bookId) {
        return toResponse(getEntity(bookId));
    }

    public BookResponse createBook(UpsertBookRequest request) {
        validateRequest(request);
        bookRepository.findByTitleIgnoreCase(request.title())
                .ifPresent(existing -> {
                    throw new DuplicateBookTitleException(request.title());
                });

        BookEntity entity = new BookEntity();
        entity.setId(UUID.randomUUID());
        apply(entity, request);
        return toResponse(bookRepository.save(entity));
    }

    public BookResponse updateBook(UUID bookId, UpsertBookRequest request) {
        validateRequest(request);
        BookEntity entity = getEntity(bookId);
        bookRepository.findByTitleIgnoreCase(request.title())
                .filter(existing -> !existing.getId().equals(bookId))
                .ifPresent(existing -> {
                    throw new DuplicateBookTitleException(request.title());
                });

        apply(entity, request);
        return toResponse(bookRepository.save(entity));
    }

    public void deleteBook(UUID bookId) {
        bookRepository.delete(getEntity(bookId));
    }

    private BookEntity getEntity(UUID bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundApplicationException(bookId));
    }

    private void validateRequest(UpsertBookRequest request) {
        if (request.status() == BookStatus.AVAILABLE && request.availableCopies() <= 0) {
            throw new InvalidBookStateException("Available books must have at least one copy.");
        }

        if (request.status() == BookStatus.ARCHIVED && request.availableCopies() > 0) {
            throw new CatalogRequestValidationException("Archived books must have zero available copies.");
        }
    }

    private void apply(BookEntity entity, UpsertBookRequest request) {
        entity.setTitle(request.title().trim());
        entity.setAuthor(request.author().trim());
        entity.setStatus(request.status());
        entity.setAvailableCopies(request.availableCopies());
    }

    private BookResponse toResponse(BookEntity entity) {
        return new BookResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getStatus(),
                entity.getAvailableCopies()
        );
    }
}
