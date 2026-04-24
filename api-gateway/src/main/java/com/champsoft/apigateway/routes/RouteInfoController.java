package com.champsoft.apigateway.routes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteInfoController {

    @Value("${services.member-service.url}")
    private String memberServiceUrl;

    @Value("${services.catalog-service.url}")
    private String catalogServiceUrl;

    @Value("${services.borrowing-service.url}")
    private String borrowingServiceUrl;

    @Value("${services.orchestrator.url}")
    private String orchestratorUrl;

    @GetMapping
    @Operation(summary = "List all gateway routes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gateway routes retrieved successfully")
    })
    public List<RouteInfoResponse> routes() {
        return List.of(
                new RouteInfoResponse("member-service", memberServiceUrl, "/members/**"),
                new RouteInfoResponse("catalog-service", catalogServiceUrl, "/books/**"),
                new RouteInfoResponse("borrowing-service", borrowingServiceUrl, "/loans/**"),
                new RouteInfoResponse("library-orchestrator", orchestratorUrl, "/borrowing-decisions/**")
        );
    }
}
