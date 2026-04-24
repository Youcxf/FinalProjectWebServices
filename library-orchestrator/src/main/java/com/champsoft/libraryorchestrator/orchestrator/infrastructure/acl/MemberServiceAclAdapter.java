package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.MemberLookupPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.MemberSnapshot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

@Component
public class MemberServiceAclAdapter implements MemberLookupPort {

    private final WebClient webClient;

    public MemberServiceAclAdapter(@Qualifier("memberWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public MemberSnapshot getMember(UUID memberId) {
        try {
            return webClient.get()
                    .uri("/api/v1/members/{memberId}", memberId)
                    .retrieve()
                    .bodyToMono(MemberSnapshot.class)
                    .block();
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Member", memberId);
            }
            throw new DownstreamDependencyException("Member service error: " + exception.getStatusCode());
        }
    }
}
