package com.champsoft.borrowingservice.borrowing.presentation;

import com.champsoft.borrowingservice.borrowing.dataaccess.LoanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class BorrowingControllerNegativePathTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository repository;

    @Test
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quantity":0,"status":"ACTIVE"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingLoanReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/loans/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateLoanReturnsConflict() throws Exception {
        repository.deleteAll();
        String body = body(UUID.randomUUID(), UUID.randomUUID(), "ACTIVE", LocalDate.now(), LocalDate.now().plusDays(7));
        mockMvc.perform(post("/api/v1/loans").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/loans").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidDateReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), UUID.randomUUID(), "ACTIVE", LocalDate.now(), LocalDate.now().minusDays(1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidReturnedLoanReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID(), UUID.randomUUID(), "RETURNED", LocalDate.now(), LocalDate.now().plusMonths(3))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest());
    }

    private String body(UUID memberId, UUID bookId, String status, LocalDate start, LocalDate due) {
        return """
                {"memberId":"%s","bookId":"%s","quantity":1,"startDate":"%s","dueDate":"%s","status":"%s"}
                """.formatted(memberId, bookId, start, due, status);
    }
}
