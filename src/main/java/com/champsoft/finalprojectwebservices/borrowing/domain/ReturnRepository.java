package com.champsoft.finalprojectwebservices.borrowing.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReturnRepository {

    List<ReturnTransaction> findAll();

    Optional<ReturnTransaction> findById(UUID id);

    Optional<ReturnTransaction> findByBorrowingTransactionId(UUID borrowingTransactionId);

    ReturnTransaction save(ReturnTransaction returnTransaction);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
