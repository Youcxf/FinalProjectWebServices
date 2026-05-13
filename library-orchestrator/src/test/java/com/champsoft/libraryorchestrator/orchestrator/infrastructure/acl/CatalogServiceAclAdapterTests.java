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
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CatalogServiceAclAdapter.class)
class CatalogServiceAclAdapterTests {

    @Autowired
    private CatalogServiceAclAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @TestConfiguration
    static class TestConfig {

        @Bean
        RestClient catalogRestClient(RestClient.Builder builder) {
            return builder.baseUrl("http://catalog-service").build();
        }
    }

    @Test
    void getsBook() {
        UUID id = UUID.randomUUID();
        server.expect(requestTo("http://catalog-service/api/v1/books/" + id))
                .andExpect(method(GET))
                .andRespond(withSuccess("""
                        {"id":"%s","title":"Book","author":"Author","status":"AVAILABLE","availableCopies":2}
                        """.formatted(id), MediaType.APPLICATION_JSON));

        assertThat(adapter.getBook(id).availableCopies()).isEqualTo(2);
    }

    @Test
    void handlesNotFoundAndServerError() {
        UUID notFound = UUID.randomUUID();
        server.expect(requestTo("http://catalog-service/api/v1/books/" + notFound)).andRespond(withResourceNotFound());
        UUID error = UUID.randomUUID();
        server.expect(requestTo("http://catalog-service/api/v1/books/" + error)).andRespond(withServerError());

        assertThrows(DownstreamResourceNotFoundApplicationException.class, () -> adapter.getBook(notFound));
        assertThrows(DownstreamDependencyException.class, () -> adapter.getBook(error));
    }
}
