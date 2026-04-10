package com.champsoft.finalprojectwebservices.book.infrastructure;

import com.champsoft.finalprojectwebservices.book.domain.Book;
import com.champsoft.finalprojectwebservices.book.domain.BookRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepositoryAdapter implements BookRepository {

    private final SpringDataBookRepository repository;

    public BookRepositoryAdapter(SpringDataBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Book save(Book book) {
        return toDomain(repository.save(toEntity(book)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private Book toDomain(BookJpaEntity entity) {
        return new Book(entity.getId(), entity.getTitle(), entity.getAuthor(), entity.getStatus(), entity.getAvailableCopies());
    }

    private BookJpaEntity toEntity(Book book) {
        BookJpaEntity entity = new BookJpaEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setStatus(book.getStatus());
        entity.setAvailableCopies(book.getAvailableCopies());
        return entity;
    }
}
