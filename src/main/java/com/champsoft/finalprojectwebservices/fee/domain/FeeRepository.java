package com.champsoft.finalprojectwebservices.fee.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeeRepository {

    List<Fee> findAll();

    Optional<Fee> findById(UUID id);

    List<Fee> findByMemberId(UUID memberId);

    Fee save(Fee fee);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
