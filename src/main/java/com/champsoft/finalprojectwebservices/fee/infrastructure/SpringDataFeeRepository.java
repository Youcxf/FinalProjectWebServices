package com.champsoft.finalprojectwebservices.fee.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataFeeRepository extends JpaRepository<FeeJpaEntity, UUID> {

    List<FeeJpaEntity> findByMemberId(UUID memberId);
}
