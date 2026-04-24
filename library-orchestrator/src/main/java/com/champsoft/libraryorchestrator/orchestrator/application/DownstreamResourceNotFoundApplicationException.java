package com.champsoft.libraryorchestrator.orchestrator.application;

import java.util.UUID;

public class DownstreamResourceNotFoundApplicationException extends RuntimeException {

    public DownstreamResourceNotFoundApplicationException(String resourceName, UUID resourceId) {
        super(resourceName + " with id " + resourceId + " was not found in a downstream service.");
    }
}
