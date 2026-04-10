package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataReturnRepository extends JpaRepository<ReturnJpaEntity, UUID> {

    Optional<ReturnJpaEntity> findByBorrowingTransactionId(UUID borrowingTransactionId);
}
