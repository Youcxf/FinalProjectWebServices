package com.champsoft.finalprojectwebservices.borrowing.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowingRepository {

    List<BorrowingTransaction> findAll();

    Optional<BorrowingTransaction> findById(UUID id);

    BorrowingTransaction save(BorrowingTransaction borrowingTransaction);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
