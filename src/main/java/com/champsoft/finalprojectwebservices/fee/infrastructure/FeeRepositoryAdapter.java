package com.champsoft.finalprojectwebservices.fee.infrastructure;

import com.champsoft.finalprojectwebservices.fee.domain.Fee;
import com.champsoft.finalprojectwebservices.fee.domain.FeeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FeeRepositoryAdapter implements FeeRepository {

    private final SpringDataFeeRepository repository;

    public FeeRepositoryAdapter(SpringDataFeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Fee> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Fee> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Fee> findByMemberId(UUID memberId) {
        return repository.findByMemberId(memberId).stream().map(this::toDomain).toList();
    }

    @Override
    public Fee save(Fee fee) {
        return toDomain(repository.save(toEntity(fee)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private Fee toDomain(FeeJpaEntity entity) {
        return new Fee(entity.getId(), entity.getMemberId(), entity.getAmount(), entity.getStatus());
    }

    private FeeJpaEntity toEntity(Fee fee) {
        FeeJpaEntity entity = new FeeJpaEntity();
        entity.setId(fee.getId());
        entity.setMemberId(fee.getMemberId());
        entity.setAmount(fee.getAmount());
        entity.setStatus(fee.getStatus());
        return entity;
    }
}
