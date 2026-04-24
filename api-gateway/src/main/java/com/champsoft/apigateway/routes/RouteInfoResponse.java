package com.champsoft.apigateway.routes;

public record RouteInfoResponse(
        String service,
        String downstreamUrl,
        String exposedPrefix
) {
}
