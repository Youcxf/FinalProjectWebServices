package com.champsoft.finalprojectwebservices.book.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataBookRepository extends JpaRepository<BookJpaEntity, UUID> {
}
