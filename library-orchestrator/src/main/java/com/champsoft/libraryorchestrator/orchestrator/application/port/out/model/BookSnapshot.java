package com.champsoft.libraryorchestrator.orchestrator.application.port.out.model;

public record BookSnapshot(
        String id,
        String title,
        String author,
        String status,
        int availableCopies
) {
}
