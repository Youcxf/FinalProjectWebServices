package com.champsoft.libraryorchestrator.orchestrator.presentation;

import com.champsoft.libraryorchestrator.orchestrator.application.BorrowingDecisionService;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.LoanSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.MemberSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionResponse;
import com.champsoft.libraryorchestrator.orchestrator.domain.EvaluateBorrowingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/borrowing-decisions")
public class BorrowingDecisionController {

    private final BorrowingDecisionService service;
    private final BorrowingDecisionModelAssembler assembler;

    public BorrowingDecisionController(BorrowingDecisionService service, BorrowingDecisionModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping
    @Operation(summary = "Retrieve all aggregated borrowing decisions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrowing decisions retrieved successfully")
    })
    public CollectionModel<EntityModel<BorrowingDecisionResponse>> getAllDecisions() {
        return CollectionModel.of(
                service.getAllDecisions().stream().map(assembler::toModel).toList(),
                linkTo(methodOn(BorrowingDecisionController.class).getAllDecisions()).withSelfRel()
        );
    }

    @GetMapping("/{decisionId}")
    @Operation(summary = "Retrieve a borrowing decision by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrowing decision found"),
            @ApiResponse(responseCode = "404", description = "Borrowing decision not found"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public EntityModel<BorrowingDecisionResponse> getDecision(@PathVariable UUID decisionId) {
        return assembler.toModel(service.getDecision(decisionId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Evaluate a borrowing decision and orchestrate a downstream loan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Borrowing decision created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Related member or book not found"),
            @ApiResponse(responseCode = "409", description = "Borrowing decision conflict"),
            @ApiResponse(responseCode = "422", description = "Borrowing rule violation"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public EntityModel<BorrowingDecisionResponse> evaluate(@Valid @RequestBody EvaluateBorrowingRequest request) {
        return assembler.toModel(service.evaluate(request));
    }

    @PutMapping("/{decisionId}")
    @Operation(summary = "Re-evaluate an existing borrowing decision")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrowing decision updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Borrowing decision not found"),
            @ApiResponse(responseCode = "409", description = "Borrowing decision conflict"),
            @ApiResponse(responseCode = "422", description = "Borrowing rule violation"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public EntityModel<BorrowingDecisionResponse> updateDecision(
            @PathVariable UUID decisionId,
            @Valid @RequestBody EvaluateBorrowingRequest request
    ) {
        return assembler.toModel(service.reevaluate(decisionId, request));
    }

    @DeleteMapping("/{decisionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a borrowing decision")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Borrowing decision deleted"),
            @ApiResponse(responseCode = "404", description = "Borrowing decision not found"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public void deleteDecision(@PathVariable UUID decisionId) {
        service.deleteDecision(decisionId);
    }

    @GetMapping("/members/{memberId}")
    @Operation(summary = "Follow a HATEOAS link to the related member")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Member found"),
            @ApiResponse(responseCode = "404", description = "Member not found"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public EntityModel<MemberSnapshot> getMember(@PathVariable UUID memberId) {
        MemberSnapshot member = service.getMember(memberId);
        return EntityModel.of(
                member,
                linkTo(methodOn(BorrowingDecisionController.class).getMember(memberId)).withSelfRel(),
                linkTo(methodOn(BorrowingDecisionController.class).getAllDecisions()).withRel("decisions")
        );
    }

    @GetMapping("/books/{bookId}")
    @Operation(summary = "Follow a HATEOAS link to the related book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public EntityModel<BookSnapshot> getBook(@PathVariable UUID bookId) {
        BookSnapshot book = service.getBook(bookId);
        return EntityModel.of(
                book,
                linkTo(methodOn(BorrowingDecisionController.class).getBook(bookId)).withSelfRel(),
                linkTo(methodOn(BorrowingDecisionController.class).getAllDecisions()).withRel("decisions")
        );
    }

    @GetMapping("/loans/{loanId}")
    @Operation(summary = "Follow a HATEOAS link to the related loan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "502", description = "Downstream dependency error")
    })
    public EntityModel<LoanSnapshot> getLoan(@PathVariable UUID loanId) {
        LoanSnapshot loan = service.getLoan(loanId);
        return EntityModel.of(
                loan,
                linkTo(methodOn(BorrowingDecisionController.class).getLoan(loanId)).withSelfRel(),
                linkTo(methodOn(BorrowingDecisionController.class).getAllDecisions()).withRel("decisions")
        );
    }
}
