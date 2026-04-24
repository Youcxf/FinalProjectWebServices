package com.champsoft.catalogservice.catalog.dataaccess;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {

    Optional<BookEntity> findByTitleIgnoreCase(String title);
}
