package com.champsoft.borrowingservice.borrowing.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.champsoft.borrowingservice.borrowing.domain.LoanStatus;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {

    List<LoanEntity> findByMemberId(UUID memberId);

    boolean existsByMemberIdAndBookIdAndStatus(UUID memberId, UUID bookId, LoanStatus status);
}
