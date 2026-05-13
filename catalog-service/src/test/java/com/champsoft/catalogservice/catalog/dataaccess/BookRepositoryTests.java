package com.champsoft.catalogservice.catalog.dataaccess;

import com.champsoft.catalogservice.catalog.domain.BookStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class BookRepositoryTests {

    @Autowired
    private BookRepository repository;

    @Test
    void savesFindsChecksAndDeletesBook() {
        BookEntity book = book("Repository Book");
        repository.save(book);

        assertThat(repository.findById(book.getId())).isPresent();
        assertThat(repository.findByTitleIgnoreCase("repository book")).isPresent();
        assertThat(repository.existsById(book.getId())).isTrue();

        repository.delete(book);

        assertThat(repository.findById(book.getId())).isEmpty();
        assertThat(repository.findByTitleIgnoreCase("Repository Book")).isEmpty();
    }

    private BookEntity book(String title) {
        BookEntity book = new BookEntity();
        book.setId(UUID.randomUUID());
        book.setTitle(title);
        book.setAuthor("Author");
        book.setStatus(BookStatus.AVAILABLE);
        book.setAvailableCopies(1);
        return book;
    }
}
