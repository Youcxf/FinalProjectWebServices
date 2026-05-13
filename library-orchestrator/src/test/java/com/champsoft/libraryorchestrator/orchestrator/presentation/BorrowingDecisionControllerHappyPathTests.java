package com.champsoft.libraryorchestrator.orchestrator.presentation;

import com.champsoft.libraryorchestrator.orchestrator.application.port.out.BookCatalogPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.LoanManagementPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.MemberLookupPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.LoanSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.MemberSnapshot;
import com.champsoft.libraryorchestrator.orchestrator.dataaccess.BorrowingDecisionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class BorrowingDecisionControllerHappyPathTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BorrowingDecisionRepository repository;

    @MockBean
    private MemberLookupPort memberLookupPort;
    @MockBean
    private BookCatalogPort bookCatalogPort;
    @MockBean
    private LoanManagementPort loanManagementPort;

    @Test
    void crudFlowWorks() throws Exception {
        repository.deleteAll();
        UUID loanId = UUID.randomUUID();
        when(memberLookupPort.getMember(any())).thenReturn(new MemberSnapshot(UUID.randomUUID().toString(), "Amy", "amy@test.com", "ACTIVE", BigDecimal.ZERO));
        when(bookCatalogPort.getBook(any())).thenReturn(new BookSnapshot(UUID.randomUUID().toString(), "Book", "Author", "AVAILABLE", 3));
        when(loanManagementPort.createLoan(any())).thenReturn(new LoanSnapshot(loanId, UUID.randomUUID(), UUID.randomUUID(), 1, "ACTIVE"));

        String json = mockMvc.perform(post("/api/v1/borrowing-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(LocalDate.now(), LocalDate.now().plusDays(7))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andReturn().getResponse().getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(json, "$.id");

        mockMvc.perform(get("/api/v1/borrowing-decisions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.borrowingDecisionResponseList", hasSize(1)));

        mockMvc.perform(get("/api/v1/borrowing-decisions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        mockMvc.perform(put("/api/v1/borrowing-decisions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(LocalDate.now(), LocalDate.now().plusDays(8))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/borrowing-decisions/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void relatedResourceLinksWork() throws Exception {
        UUID id = UUID.randomUUID();
        when(memberLookupPort.getMember(id)).thenReturn(new MemberSnapshot(id.toString(), "Amy", "amy@test.com", "ACTIVE", BigDecimal.ZERO));
        when(bookCatalogPort.getBook(id)).thenReturn(new BookSnapshot(id.toString(), "Book", "Author", "AVAILABLE", 1));
        when(loanManagementPort.getLoan(id)).thenReturn(new LoanSnapshot(id, UUID.randomUUID(), UUID.randomUUID(), 1, "ACTIVE"));

        mockMvc.perform(get("/api/v1/borrowing-decisions/members/{id}", id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/borrowing-decisions/books/{id}", id)).andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/borrowing-decisions/loans/{id}", id)).andExpect(status().isOk());
    }

    private String body(LocalDate start, LocalDate due) {
        return """
                {"memberId":"%s","bookId":"%s","quantity":1,"startDate":"%s","dueDate":"%s"}
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), start, due);
    }
}
