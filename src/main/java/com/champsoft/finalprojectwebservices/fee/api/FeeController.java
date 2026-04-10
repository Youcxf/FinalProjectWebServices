package com.champsoft.finalprojectwebservices.fee.api;

import com.champsoft.finalprojectwebservices.fee.application.FeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @GetMapping
    public ResponseEntity<List<FeeResponse>> getAll() {
        return ResponseEntity.ok(feeService.getAll().stream().map(FeeMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(FeeMapper.toResponse(feeService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<FeeResponse> create(@Valid @RequestBody FeeRequest request) {
        FeeResponse response = FeeMapper.toResponse(feeService.create(FeeMapper.toDomain(UUID.randomUUID(), request)));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeResponse> update(@PathVariable UUID id, @Valid @RequestBody FeeRequest request) {
        return ResponseEntity.ok(FeeMapper.toResponse(feeService.update(id, FeeMapper.toDomain(id, request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        feeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
