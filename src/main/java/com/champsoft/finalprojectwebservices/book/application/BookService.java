package com.champsoft.finalprojectwebservices.book.application;

import com.champsoft.finalprojectwebservices.book.domain.Book;
import com.champsoft.finalprojectwebservices.book.domain.BookRepository;
import com.champsoft.finalprojectwebservices.shared.domain.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Book getById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public Book update(UUID id, Book updatedBook) {
        Book existing = getById(id);
        existing.update(updatedBook.getTitle(), updatedBook.getAuthor(), updatedBook.getStatus(), updatedBook.getAvailableCopies());
        return bookRepository.save(existing);
    }

    public void delete(UUID id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found: " + id);
        }
        bookRepository.deleteById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
