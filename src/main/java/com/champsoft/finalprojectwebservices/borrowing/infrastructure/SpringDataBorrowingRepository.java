package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataBorrowingRepository extends JpaRepository<BorrowingJpaEntity, UUID> {
}
