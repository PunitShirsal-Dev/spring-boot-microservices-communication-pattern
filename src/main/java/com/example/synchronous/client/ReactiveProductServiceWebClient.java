package com.example.synchronous.client;

import com.example.synchronous.dto.ProductDTO;
import com.example.synchronous.dto.ProductReservationRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class ReactiveProductServiceWebClient {

    private final WebClient webClient;

    public ReactiveProductServiceWebClient(WebClient.Builder webClientBuilder, @Value("${service.product.url}") String productServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(productServiceUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
                    String correlationId = MDC.get("correlationId");
                    return Mono.just(ClientRequest.from(request).header("X-Correlation-ID", correlationId).build());}))
                .build();
    }

    public Mono<ProductDTO> getProductById(Long productId) {
        return webClient
                .get()
                .uri("/api/v1/products/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry
                        .backoff(3, Duration.ofMillis(100))
                        .filter(throwable -> throwable instanceof WebClientResponseException.ServiceUnavailable)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new ServiceUnavailableException("Product service unavailable after retries")))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.just(getFallbackProduct(productId)))
                .doOnError(error -> log.error("Failed to fetch product: {}", productId, error));
    }

    public Mono<Boolean> reserveProducts(List<ProductReservationRequest> request) {
        return webClient
                .post()
                .uri("/api/v1/products/reserve")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(10));
    }

    private ProductDTO getFallbackProduct(Long productId) {
        return new ProductDTO(productId, "", "", "", new BigDecimal(1), new BigDecimal(1),
                new BigDecimal(1), 1, 1, 2, "", List.of("A", "B"),
                "", "", "", List.of("Tag-A", "Tag-B"), 1.0, 1, 1, true,
                true, true, LocalDateTime.now(), LocalDateTime.now(), "", "", new HashMap<>(), "1", 1L);
    }
}
