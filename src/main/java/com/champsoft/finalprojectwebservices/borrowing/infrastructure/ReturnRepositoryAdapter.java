package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import com.champsoft.finalprojectwebservices.borrowing.domain.ReturnRepository;
import com.champsoft.finalprojectwebservices.borrowing.domain.ReturnTransaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReturnRepositoryAdapter implements ReturnRepository {

    private final SpringDataReturnRepository repository;

    public ReturnRepositoryAdapter(SpringDataReturnRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ReturnTransaction> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ReturnTransaction> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ReturnTransaction> findByBorrowingTransactionId(UUID borrowingTransactionId) {
        return repository.findByBorrowingTransactionId(borrowingTransactionId).map(this::toDomain);
    }

    @Override
    public ReturnTransaction save(ReturnTransaction returnTransaction) {
        return toDomain(repository.save(toEntity(returnTransaction)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private ReturnTransaction toDomain(ReturnJpaEntity entity) {
        return new ReturnTransaction(entity.getId(), entity.getBorrowingTransactionId(), entity.getReturnDate(), entity.getReturnStatus());
    }

    private ReturnJpaEntity toEntity(ReturnTransaction domain) {
        ReturnJpaEntity entity = new ReturnJpaEntity();
        entity.setId(domain.getId());
        entity.setBorrowingTransactionId(domain.getBorrowingTransactionId());
        entity.setReturnDate(domain.getReturnDate());
        entity.setReturnStatus(domain.getReturnStatus());
        return entity;
    }
}
