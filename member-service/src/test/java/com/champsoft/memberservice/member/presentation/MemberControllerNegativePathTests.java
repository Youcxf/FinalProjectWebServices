package com.champsoft.memberservice.member.presentation;

import com.champsoft.memberservice.member.dataaccess.MemberRepository;
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
class MemberControllerNegativePathTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository repository;

    @Test
    void invalidRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"bad","status":"ACTIVE","outstandingFees":0}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingMemberReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/members/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateMemberReturnsConflict() throws Exception {
        repository.deleteAll();
        String body = """
                {"name":"Amy","email":"duplicate@test.com","status":"ACTIVE","outstandingFees":0}
                """;
        mockMvc.perform(post("/api/v1/members").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/members").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidStateReturnsUnprocessableEntity() throws Exception {
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Amy","email":"fees@test.com","status":"ACTIVE","outstandingFees":5}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json"))
                .andExpect(status().isBadRequest());
    }
}
