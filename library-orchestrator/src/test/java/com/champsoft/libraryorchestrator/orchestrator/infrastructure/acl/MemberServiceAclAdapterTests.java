package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

@RestClientTest(MemberServiceAclAdapter.class)
class MemberServiceAclAdapterTests {

    @Autowired
    private MemberServiceAclAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @TestConfiguration
    static class TestConfig {

        @Bean
        RestClient memberRestClient(RestClient.Builder builder) {
            return builder.baseUrl("http://member-service").build();
        }
    }

    @Test
    void getsMember() {
        UUID id = UUID.randomUUID();
        server.expect(requestTo("http://member-service/api/v1/members/" + id))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        {"id":"%s","name":"Amy","email":"amy@test.com","status":"ACTIVE","outstandingFees":0}
                        """.formatted(id), MediaType.APPLICATION_JSON));

        assertThat(adapter.getMember(id).status()).isEqualTo("ACTIVE");
    }

    @Test
    void handlesNotFoundAndServerError() {
        UUID notFound = UUID.randomUUID();
        server.expect(requestTo("http://member-service/api/v1/members/" + notFound)).andRespond(withResourceNotFound());
        UUID error = UUID.randomUUID();
        server.expect(requestTo("http://member-service/api/v1/members/" + error)).andRespond(withServerError());

        assertThrows(DownstreamResourceNotFoundApplicationException.class, () -> adapter.getMember(notFound));
        assertThrows(DownstreamDependencyException.class, () -> adapter.getMember(error));
    }
}
