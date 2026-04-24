package com.champsoft.borrowingservice.borrowing.presentation;

import com.champsoft.borrowingservice.borrowing.application.BorrowingService;
import com.champsoft.borrowingservice.borrowing.domain.LoanResponse;
import com.champsoft.borrowingservice.borrowing.domain.UpsertLoanRequest;
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
@RequestMapping("/api/v1/loans")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all loans")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loans retrieved successfully")
    })
    public List<LoanResponse> getAllLoans() {
        return borrowingService.getAllLoans();
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Retrieve a loan by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan found"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public LoanResponse getLoan(@PathVariable UUID loanId) {
        return borrowingService.getLoanById(loanId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loan created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Duplicate active loan"),
            @ApiResponse(responseCode = "422", description = "Invalid loan state")
    })
    public LoanResponse createLoan(@Valid @RequestBody UpsertLoanRequest request) {
        return borrowingService.createLoan(request);
    }

    @PutMapping("/{loanId}")
    @Operation(summary = "Update a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "422", description = "Invalid loan state")
    })
    public LoanResponse updateLoan(@PathVariable UUID loanId, @Valid @RequestBody UpsertLoanRequest request) {
        return borrowingService.updateLoan(loanId, request);
    }

    @DeleteMapping("/{loanId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a loan")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Loan deleted"),
            @ApiResponse(responseCode = "404", description = "Loan not found")
    })
    public void deleteLoan(@PathVariable UUID loanId) {
        borrowingService.deleteLoan(loanId);
    }
}
