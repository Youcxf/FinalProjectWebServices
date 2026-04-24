package com.champsoft.catalogservice.catalog.presentation;

import com.champsoft.catalogservice.catalog.application.CatalogService;
import com.champsoft.catalogservice.catalog.domain.BookResponse;
import com.champsoft.catalogservice.catalog.domain.UpsertBookRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/books")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully")
    })
    public List<BookResponse> getAllBooks() {
        return catalogService.getAllBooks();
    }

    @GetMapping("/{bookId}")
    @Operation(summary = "Retrieve a book by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public BookResponse getBook(@PathVariable UUID bookId) {
        return catalogService.getBookById(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a book")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Book created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Duplicate book title"),
            @ApiResponse(responseCode = "422", description = "Invalid book state")
    })
    public BookResponse createBook(@Valid @RequestBody UpsertBookRequest request) {
        return catalogService.createBook(request);
    }

    @PutMapping("/{bookId}")
    @Operation(summary = "Update a book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate book title"),
            @ApiResponse(responseCode = "422", description = "Invalid book state")
    })
    public BookResponse updateBook(@PathVariable UUID bookId, @Valid @RequestBody UpsertBookRequest request) {
        return catalogService.updateBook(bookId, request);
    }

    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    public void deleteBook(@PathVariable UUID bookId) {
        catalogService.deleteBook(bookId);
    }
}
