package com.champsoft.finalprojectwebservices.borrowing.infrastructure;

import com.champsoft.finalprojectwebservices.borrowing.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BorrowingRepositoryAdapter implements BorrowingRepository {

    private final SpringDataBorrowingRepository repository;

    public BorrowingRepositoryAdapter(SpringDataBorrowingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BorrowingTransaction> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BorrowingTransaction> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public BorrowingTransaction save(BorrowingTransaction borrowingTransaction) {
        return toDomain(repository.save(toEntity(borrowingTransaction)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private BorrowingTransaction toDomain(BorrowingJpaEntity entity) {
        return new BorrowingTransaction(
                entity.getId(),
                entity.getMemberId(),
                entity.getStatus(),
                new BorrowingPeriod(entity.getStartDate(), entity.getDueDate()),
                entity.getItems().stream()
                        .map(item -> new BorrowedItem(item.getId(), item.getBookId(), item.getQuantity()))
                        .toList()
        );
    }

    private BorrowingJpaEntity toEntity(BorrowingTransaction domain) {
        BorrowingJpaEntity entity = new BorrowingJpaEntity();
        entity.setId(domain.getId());
        entity.setMemberId(domain.getMemberId());
        entity.setStatus(domain.getStatus());
        entity.setStartDate(domain.getBorrowingPeriod().startDate());
        entity.setDueDate(domain.getBorrowingPeriod().dueDate());
        entity.setItems(domain.getBorrowedItems().stream().map(item -> {
            BorrowedItemJpaEntity itemEntity = new BorrowedItemJpaEntity();
            itemEntity.setId(item.getId());
            itemEntity.setBookId(item.getBookId());
            itemEntity.setQuantity(item.getQuantity());
            itemEntity.setBorrowingTransaction(entity);
            return itemEntity;
        }).toList());
        return entity;
    }
}
