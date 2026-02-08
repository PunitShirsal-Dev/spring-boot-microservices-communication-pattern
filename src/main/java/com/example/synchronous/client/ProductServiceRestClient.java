package com.example.synchronous.client;

import com.example.synchronous.dto.ProductDTO;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductServiceRestClient {

    private final RestTemplate restTemplate;

    public ProductServiceRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductDTO processProduct(ProductDTO request) {
        String url = "http://product-service/api/v1/products";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Correlation-ID", MDC.get("correlationId"));

        HttpEntity<ProductDTO> entity = new HttpEntity<>(request, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ProductDTO.class
        ).getBody();
    }
}
