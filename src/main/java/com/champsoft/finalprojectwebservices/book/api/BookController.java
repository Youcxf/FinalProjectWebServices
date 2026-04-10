package com.champsoft.finalprojectwebservices.book.api;

import com.champsoft.finalprojectwebservices.book.application.BookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        return ResponseEntity.ok(bookService.getAll().stream().map(BookMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(BookMapper.toResponse(bookService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        BookResponse response = BookMapper.toResponse(bookService.create(BookMapper.toDomain(UUID.randomUUID(), request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable UUID id, @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(BookMapper.toResponse(bookService.update(id, BookMapper.toDomain(id, request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
