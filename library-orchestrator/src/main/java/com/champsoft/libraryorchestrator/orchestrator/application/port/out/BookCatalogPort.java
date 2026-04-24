package com.champsoft.libraryorchestrator.orchestrator.application.port.out;

import com.champsoft.libraryorchestrator.orchestrator.application.port.out.model.BookSnapshot;

import java.util.UUID;

public interface BookCatalogPort {

    BookSnapshot getBook(UUID bookId);
}
