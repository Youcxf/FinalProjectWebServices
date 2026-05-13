package com.champsoft.apigateway.routes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("testing")
class GatewayRouteTests {

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void expectedRoutesExist() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();

        assertThat(routes).isNotNull();
        assertThat(routes).extracting(Route::getId)
                .contains(
                        "member-service-root",
                        "member-service-nested",
                        "catalog-service-root",
                        "catalog-service-nested",
                        "borrowing-service-root",
                        "borrowing-service-nested",
                        "library-orchestrator-root",
                        "library-orchestrator-nested"
                );
        assertThat(routes).anyMatch(route -> route.getId().equals("member-service-root")
                && route.getUri().toString().equals("http://localhost:8081"));
    }

    @Test
    void routeInfoEndpointWorks() {
        webTestClient.get()
                .uri("/api/v1/routes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].service").isEqualTo("member-service")
                .jsonPath("$[3].service").isEqualTo("library-orchestrator");
    }

    @Test
    void unknownRouteReturnsNotFound() {
        webTestClient.get()
                .uri("/no-such-route")
                .exchange()
                .expectStatus().isNotFound();
    }
}
