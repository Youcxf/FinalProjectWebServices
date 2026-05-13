package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.CreateLoanCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BorrowingServiceAclAdapter.class)
class BorrowingServiceAclAdapterTests {

    @Autowired
    private BorrowingServiceAclAdapter adapter;

    @Autowired
    private MockRestServiceServer server;

    @TestConfiguration
    static class TestConfig {

        @Bean
        RestClient borrowingRestClient(RestClient.Builder builder) {
            return builder.baseUrl("http://borrowing-service").build();
        }
    }

    @Test
    void getsCreatesAndDeletesLoan() {
        UUID id = UUID.randomUUID();
        server.expect(requestTo("http://borrowing-service/api/v1/loans/" + id))
                .andExpect(method(GET))
                .andRespond(withSuccess(loanJson(id), MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://borrowing-service/api/v1/loans"))
                .andExpect(method(POST))
                .andRespond(withSuccess(loanJson(id), MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://borrowing-service/api/v1/loans/" + id))
                .andExpect(method(DELETE))
                .andRespond(withSuccess());

        assertThat(adapter.getLoan(id).id()).isEqualTo(id);
        assertThat(adapter.createLoan(new CreateLoanCommand(UUID.randomUUID(), UUID.randomUUID(), 1, LocalDate.now(), LocalDate.now().plusDays(7))).id())
                .isEqualTo(id);
        adapter.deleteLoan(id);
    }

    @Test
    void handlesNotFoundAndServerError() {
        UUID notFound = UUID.randomUUID();
        server.expect(requestTo("http://borrowing-service/api/v1/loans/" + notFound)).andRespond(withResourceNotFound());
        UUID error = UUID.randomUUID();
        server.expect(requestTo("http://borrowing-service/api/v1/loans/" + error)).andRespond(withServerError());

        assertThrows(DownstreamResourceNotFoundApplicationException.class, () -> adapter.getLoan(notFound));
        assertThrows(DownstreamDependencyException.class, () -> adapter.getLoan(error));
    }

    private String loanJson(UUID id) {
        return """
                {"id":"%s","memberId":"%s","bookId":"%s","quantity":1,"status":"ACTIVE"}
                """.formatted(id, UUID.randomUUID(), UUID.randomUUID());
    }
}
