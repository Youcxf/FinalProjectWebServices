package com.champsoft.libraryorchestrator.orchestrator.presentation;

import com.champsoft.libraryorchestrator.orchestrator.application.port.out.BookCatalogPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.LoanManagementPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.MemberLookupPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class BorrowingDecisionControllerNegativePathTests {

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
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/borrowing-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quantity":0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingDecisionReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/borrowing-decisions/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidDatesReturnConflict() throws Exception {
        mockMvc.perform(post("/api/v1/borrowing-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(LocalDate.now(), LocalDate.now().minusDays(1))))
                .andExpect(status().isConflict());
    }

    @Test
    void tooManyCopiesReturnsUnprocessableEntity() throws Exception {
        repository.deleteAll();
        when(memberLookupPort.getMember(any())).thenReturn(new MemberSnapshot(UUID.randomUUID().toString(), "Amy", "amy@test.com", "ACTIVE", BigDecimal.ZERO));
        when(bookCatalogPort.getBook(any())).thenReturn(new BookSnapshot(UUID.randomUUID().toString(), "Book", "Author", "AVAILABLE", 0));

        mockMvc.perform(post("/api/v1/borrowing-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(LocalDate.now(), LocalDate.now().plusDays(7))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/borrowing-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest());
    }

    private String body(LocalDate start, LocalDate due) {
        return """
                {"memberId":"%s","bookId":"%s","quantity":1,"startDate":"%s","dueDate":"%s"}
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), start, due);
    }
}
