package com.champsoft.finalprojectwebservices.borrowing.api;

import com.champsoft.finalprojectwebservices.borrowing.application.BorrowingFacade;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingFacade borrowingFacade;

    public BorrowingController(BorrowingFacade borrowingFacade) {
        this.borrowingFacade = borrowingFacade;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<BorrowingResponse>>> getAll() {
        List<EntityModel<BorrowingResponse>> models = borrowingFacade.getBorrowingViews().stream()
                .map(this::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(BorrowingController.class).getAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BorrowingResponse>> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(toModel(borrowingFacade.getBorrowingView(id)));
    }

    @PostMapping
    public ResponseEntity<EntityModel<BorrowingResponse>> create(@Valid @RequestBody BorrowingRequest request) {
        BorrowingResponse response = borrowingFacade.getBorrowingView(borrowingFacade.createBorrowing(request).getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(toModel(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<BorrowingResponse>> update(@PathVariable UUID id, @Valid @RequestBody BorrowingRequest request) {
        borrowingFacade.updateBorrowing(id, request);
        return ResponseEntity.ok(toModel(borrowingFacade.getBorrowingView(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        borrowingFacade.deleteBorrowing(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<BorrowingResponse> toModel(BorrowingResponse response) {
        EntityModel<BorrowingResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(BorrowingController.class).getOne(response.id())).withSelfRel());
        model.add(linkTo(methodOn(BorrowingController.class).getAll()).withRel("borrowings"));
        if (response.returnSummary() == null) {
            model.add(linkTo(methodOn(ReturnController.class).create(new ReturnRequest(response.id(), response.dueDate()))).withRel("return"));
        } else {
            model.add(linkTo(methodOn(ReturnController.class).getOne(response.returnSummary().id())).withRel("return-details"));
        }
        return model;
    }
}
