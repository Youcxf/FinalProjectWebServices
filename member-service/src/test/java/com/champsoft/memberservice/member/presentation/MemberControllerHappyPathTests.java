package com.champsoft.memberservice.member.presentation;

import com.champsoft.memberservice.member.dataaccess.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
class MemberControllerHappyPathTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository repository;

    @Test
    void crudFlowWorks() throws Exception {
        repository.deleteAll();

        String location = mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Amy","email":"amy.controller@test.com","status":"ACTIVE","outstandingFees":0}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("amy.controller@test.com"))
                .andReturn().getResponse().getContentAsString();

        String id = com.jayway.jsonpath.JsonPath.read(location, "$.id");

        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/v1/members/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Amy"));

        mockMvc.perform(put("/api/v1/members/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Amy Updated","email":"amy.updated@test.com","status":"ACTIVE","outstandingFees":0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Amy Updated"));

        mockMvc.perform(delete("/api/v1/members/{id}", id))
                .andExpect(status().isNoContent());
    }
}
