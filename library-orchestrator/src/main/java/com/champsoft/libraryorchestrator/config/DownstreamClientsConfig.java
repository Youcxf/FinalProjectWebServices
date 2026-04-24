package com.champsoft.libraryorchestrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DownstreamClientsConfig {

    @Bean
    public WebClient memberWebClient(
            WebClient.Builder builder,
            @Value("${clients.member-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient catalogWebClient(
            WebClient.Builder builder,
            @Value("${clients.catalog-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient borrowingWebClient(
            WebClient.Builder builder,
            @Value("${clients.borrowing-service.base-url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }
}
