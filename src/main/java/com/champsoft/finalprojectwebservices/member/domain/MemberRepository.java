package com.champsoft.finalprojectwebservices.member.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {

    List<Member> findAll();

    Optional<Member> findById(UUID id);

    Optional<Member> findByEmail(String email);

    Member save(Member member);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
