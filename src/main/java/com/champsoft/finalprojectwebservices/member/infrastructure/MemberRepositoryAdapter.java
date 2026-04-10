package com.champsoft.finalprojectwebservices.member.infrastructure;

import com.champsoft.finalprojectwebservices.member.domain.Member;
import com.champsoft.finalprojectwebservices.member.domain.MemberRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberRepositoryAdapter implements MemberRepository {

    private final SpringDataMemberRepository repository;

    public MemberRepositoryAdapter(SpringDataMemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Member> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Member> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Member save(Member member) {
        return toDomain(repository.save(toEntity(member)));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    private Member toDomain(MemberJpaEntity entity) {
        return new Member(entity.getId(), entity.getName(), entity.getEmail(), entity.getStatus(), entity.getOutstandingFees());
    }

    private MemberJpaEntity toEntity(Member member) {
        MemberJpaEntity entity = new MemberJpaEntity();
        entity.setId(member.getId());
        entity.setName(member.getName());
        entity.setEmail(member.getEmail());
        entity.setStatus(member.getStatus());
        entity.setOutstandingFees(member.getOutstandingFees());
        return entity;
    }
}
