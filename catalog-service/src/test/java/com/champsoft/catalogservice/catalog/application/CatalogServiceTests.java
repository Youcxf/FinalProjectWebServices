package com.champsoft.catalogservice.catalog.application;

import com.champsoft.catalogservice.catalog.dataaccess.BookEntity;
import com.champsoft.catalogservice.catalog.dataaccess.BookRepository;
import com.champsoft.catalogservice.catalog.domain.BookResponse;
import com.champsoft.catalogservice.catalog.domain.BookStatus;
import com.champsoft.catalogservice.catalog.domain.DuplicateBookTitleException;
import com.champsoft.catalogservice.catalog.domain.InvalidBookStateException;
import com.champsoft.catalogservice.catalog.domain.UpsertBookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTests {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private CatalogService service;

    @Test
    void createsBook() {
        when(repository.findByTitleIgnoreCase("Clean Code")).thenReturn(Optional.empty());
        when(repository.save(org.mockito.ArgumentMatchers.any(BookEntity.class))).thenAnswer(call -> call.getArgument(0));

        BookResponse response = service.createBook(request("Clean Code", BookStatus.AVAILABLE, 2));

        assertThat(response.title()).isEqualTo("Clean Code");
    }

    @Test
    void createRejectsDuplicateTitle() {
        when(repository.findByTitleIgnoreCase("Clean Code")).thenReturn(Optional.of(entity(UUID.randomUUID(), "Clean Code")));

        assertThrows(DuplicateBookTitleException.class,
                () -> service.createBook(request("Clean Code", BookStatus.AVAILABLE, 2)));
    }

    @Test
    void createRejectsAvailableBookWithoutCopies() {
        assertThrows(InvalidBookStateException.class,
                () -> service.createBook(request("Clean Code", BookStatus.AVAILABLE, 0)));
    }

    @Test
    void createRejectsArchivedBookWithCopies() {
        assertThrows(CatalogRequestValidationException.class,
                () -> service.createBook(request("Clean Code", BookStatus.ARCHIVED, 1)));
    }

    @Test
    void getsListsUpdatesAndDeletesBook() {
        UUID id = UUID.randomUUID();
        BookEntity entity = entity(id, "Old");
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findAll()).thenReturn(List.of(entity));
        when(repository.findByTitleIgnoreCase("New")).thenReturn(Optional.empty());
        when(repository.save(entity)).thenReturn(entity);

        assertThat(service.getBookById(id).id()).isEqualTo(id);
        assertThat(service.getAllBooks()).hasSize(1);
        assertThat(service.updateBook(id, request("New", BookStatus.AVAILABLE, 1)).title()).isEqualTo("New");

        service.deleteBook(id);

        verify(repository).delete(entity);
    }

    @Test
    void updateAllowsExistingTitleOnSameBook() {
        UUID id = UUID.randomUUID();
        BookEntity entity = entity(id, "Clean Code");
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.findByTitleIgnoreCase("Clean Code")).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        BookResponse response = service.updateBook(id, request("Clean Code", BookStatus.AVAILABLE, 2));

        assertThat(response.title()).isEqualTo("Clean Code");
    }

    @Test
    void updateRejectsExistingTitleOnDifferentBook() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(entity(id, "Old")));
        when(repository.findByTitleIgnoreCase("Clean Code"))
                .thenReturn(Optional.of(entity(UUID.randomUUID(), "Clean Code")));

        assertThrows(DuplicateBookTitleException.class,
                () -> service.updateBook(id, request("Clean Code", BookStatus.AVAILABLE, 2)));
    }

    @Test
    void missingBookThrowsException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundApplicationException.class, () -> service.getBookById(id));
    }

    private UpsertBookRequest request(String title, BookStatus status, int copies) {
        return new UpsertBookRequest(title, "Author", status, copies);
    }

    private BookEntity entity(UUID id, String title) {
        BookEntity entity = new BookEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setAuthor("Author");
        entity.setStatus(BookStatus.AVAILABLE);
        entity.setAvailableCopies(1);
        return entity;
    }
}
