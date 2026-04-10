package com.champsoft.finalprojectwebservices.borrowing.api;

import com.champsoft.finalprojectwebservices.borrowing.application.BorrowingFacade;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final BorrowingFacade borrowingFacade;

    public ReturnController(BorrowingFacade borrowingFacade) {
        this.borrowingFacade = borrowingFacade;
    }

    @GetMapping
    public ResponseEntity<List<ReturnResponse>> getAll() {
        return ResponseEntity.ok(borrowingFacade.getAllReturns().stream().map(borrowingFacade::toReturnResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(borrowingFacade.toReturnResponse(borrowingFacade.getReturnById(id)));
    }

    @PostMapping
    public ResponseEntity<ReturnResponse> create(@Valid @RequestBody ReturnRequest request) {
        ReturnResponse response = borrowingFacade.toReturnResponse(
                borrowingFacade.createReturn(request.borrowingTransactionId(), request.returnDate())
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnResponse> update(@PathVariable UUID id, @Valid @RequestBody ReturnRequest request) {
        return ResponseEntity.ok(
                borrowingFacade.toReturnResponse(
                        borrowingFacade.updateReturn(id, request.borrowingTransactionId(), request.returnDate())
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        borrowingFacade.deleteReturn(id);
        return ResponseEntity.noContent().build();
    }
}
