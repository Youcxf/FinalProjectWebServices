package com.champsoft.libraryorchestrator.orchestrator.presentation;

import com.champsoft.libraryorchestrator.orchestrator.domain.BorrowingDecisionResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BorrowingDecisionModelAssembler implements RepresentationModelAssembler<BorrowingDecisionResponse, EntityModel<BorrowingDecisionResponse>> {

    @Override
    public EntityModel<BorrowingDecisionResponse> toModel(BorrowingDecisionResponse response) {
        EntityModel<BorrowingDecisionResponse> model = EntityModel.of(response);
        model.add(linkTo(methodOn(BorrowingDecisionController.class).getDecision(response.id())).withSelfRel());
        model.add(linkTo(methodOn(BorrowingDecisionController.class).getAllDecisions()).withRel("decisions"));
        model.add(linkTo(BorrowingDecisionController.class).slash(response.id()).withRel("delete-decision"));
        model.add(linkTo(BorrowingDecisionController.class).slash("members").slash(response.memberId()).withRel("member"));
        model.add(linkTo(BorrowingDecisionController.class).slash("books").slash(response.bookId()).withRel("book"));
        if (response.loanId() != null) {
            model.add(linkTo(BorrowingDecisionController.class).slash("loans").slash(response.loanId()).withRel("loan"));
        }
        return model;
    }
}
