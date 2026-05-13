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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class BorrowingControllerHappyPathTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository repository;

    @Test
    void crudFlowWorks() throws Exception {
        repository.deleteAll();

        String memberId = UUID.randomUUID().toString();
        String bookId = UUID.randomUUID().toString();
        String start = LocalDate.now().toString();
        String due = LocalDate.now().plusDays(7).toString();

        String json = mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":"%s","bookId":"%s","quantity":1,"startDate":"%s","dueDate":"%s","status":"ACTIVE"}
                                """.formatted(memberId, bookId, start, due)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(memberId))
                .andReturn().getResponse().getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(json, "$.id");

        mockMvc.perform(get("/api/v1/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/loans/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId").value(bookId));

        mockMvc.perform(put("/api/v1/loans/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId":"%s","bookId":"%s","quantity":1,"startDate":"%s","dueDate":"%s","status":"RETURNED"}
                                """.formatted(memberId, bookId, start, due)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        mockMvc.perform(delete("/api/v1/loans/{id}", id))
                .andExpect(status().isNoContent());
    }
}
