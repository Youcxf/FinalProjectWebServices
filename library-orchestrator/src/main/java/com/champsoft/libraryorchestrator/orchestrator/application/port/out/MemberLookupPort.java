package com.champsoft.libraryorchestrator.orchestrator.application.port.out;

import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.MemberSnapshot;

import java.util.UUID;

public interface MemberLookupPort {

    MemberSnapshot getMember(UUID memberId);
}
