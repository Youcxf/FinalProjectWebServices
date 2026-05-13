package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.BookCatalogPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class CatalogServiceAclAdapter implements BookCatalogPort {

    private final RestClient restClient;

    public CatalogServiceAclAdapter(@Qualifier("catalogRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public BookSnapshot getBook(UUID bookId) {
        try {
            return restClient.get()
                    .uri("/api/v1/books/{bookId}", bookId)
                    .retrieve()
                    .body(BookSnapshot.class);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Book", bookId);
            }
            throw new DownstreamDependencyException("Catalog service error: " + exception.getStatusCode());
        }
    }
}
