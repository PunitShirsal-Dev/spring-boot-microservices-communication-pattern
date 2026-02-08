package com.example.synchronous.client;

import com.example.synchronous.fallback.ProductServiceFeignFallback;
import com.example.synchronous.config.ProductServiceFeignClientConfig;
import com.example.synchronous.dto.ProductAvailabilityRequest;
import com.example.synchronous.dto.ProductReservationRequest;
import com.example.synchronous.dto.StockUpdateRequest;
import com.example.synchronous.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "product-service",
        url = "${feign.client.product-service.url}",
        configuration = ProductServiceFeignClientConfig.class,
        fallback = ProductServiceFeignFallback.class
)
public interface ProductServiceFeignClient {

    @GetMapping("/api/v1/products/{productId}")
    ProductDTO getProductById(@PathVariable Long productId);

    @PostMapping("/api/v1/products/check-availability")
    Boolean checkProductAvailability(@RequestBody List<ProductAvailabilityRequest> request);

    @PostMapping("/api/v1/products/reserve")
    Boolean reserveProducts(@RequestBody List<ProductReservationRequest> request);

    @PutMapping("/api/v1/products/{productId}/stock")
    void updateStock(@PathVariable Long productId, @RequestBody StockUpdateRequest request);
}