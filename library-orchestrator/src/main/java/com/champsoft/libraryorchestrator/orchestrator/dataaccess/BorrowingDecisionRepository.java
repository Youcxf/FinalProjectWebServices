package com.champsoft.libraryorchestrator.orchestrator.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BorrowingDecisionRepository extends JpaRepository<BorrowingDecisionEntity, UUID> {

    List<BorrowingDecisionEntity> findByMemberId(UUID memberId);

    boolean existsByMemberId(UUID memberId);
}
