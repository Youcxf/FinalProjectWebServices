package com.champsoft.catalogservice.catalog.presentation;

import com.champsoft.catalogservice.catalog.dataaccess.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class CatalogControllerNegativePathTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository repository;

    @Test
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","author":"","status":"AVAILABLE","availableCopies":1}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingBookReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/books/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateBookReturnsConflict() throws Exception {
        repository.deleteAll();
        String body = """
                {"title":"Duplicate Book","author":"Author","status":"AVAILABLE","availableCopies":1}
                """;
        mockMvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidStateReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Bad State","author":"Author","status":"AVAILABLE","availableCopies":0}
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest());
    }
}
