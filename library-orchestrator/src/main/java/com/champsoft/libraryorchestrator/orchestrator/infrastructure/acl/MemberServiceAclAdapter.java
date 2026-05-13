package com.champsoft.libraryorchestrator.orchestrator.infrastructure.acl;

import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamDependencyException;
import com.champsoft.libraryorchestrator.orchestrator.application.DownstreamResourceNotFoundApplicationException;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.MemberLookupPort;
import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.MemberSnapshot;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class MemberServiceAclAdapter implements MemberLookupPort {

    private final RestClient restClient;

    public MemberServiceAclAdapter(@Qualifier("memberRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public MemberSnapshot getMember(UUID memberId) {
        try {
            return restClient.get()
                    .uri("/api/v1/members/{memberId}", memberId)
                    .retrieve()
                    .body(MemberSnapshot.class);
        } catch (HttpStatusCodeException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new DownstreamResourceNotFoundApplicationException("Member", memberId);
            }
            throw new DownstreamDependencyException("Member service error: " + exception.getStatusCode());
        }
    }
}
