package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.BookCatalogPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
public class CatalogServiceAclAdapter implements BookCatalogPort {

    private final WebClient webClient;

    public CatalogServiceAclAdapter(@Qualifier("catalogWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public BookSnapshot getBook(UUID bookId) {
        try {
            return webClient.get()
                    .uri("/api/v1/books/{bookId}", bookId)
                    .retrieve()
                    .bodyToMono(BookSnapshot.class)
                    .block();
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Book", bookId);
            }
            throw new DownstreamDependencyException("Catalog service error: " + exception.getStatusCode());
        }
    }
}
