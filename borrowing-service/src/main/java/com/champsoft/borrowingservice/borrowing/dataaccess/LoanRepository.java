package com.champsoft.borrowingservice.borrowing.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<LoanEntity, UUID> {
}
