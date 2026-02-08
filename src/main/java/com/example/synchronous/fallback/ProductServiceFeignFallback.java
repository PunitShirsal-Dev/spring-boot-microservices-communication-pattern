package com.example.synchronous.fallback;

import com.example.synchronous.client.ProductServiceFeignClient;
import com.example.synchronous.dto.ProductAvailabilityRequest;
import com.example.synchronous.dto.ProductDTO;
import com.example.synchronous.dto.ProductReservationRequest;
import com.example.synchronous.dto.StockUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class ProductServiceFeignFallback implements ProductServiceFeignClient {

    @Override
    public ProductDTO getProductById(Long productId) {
        // Return cached product or default
        return ProductDTO.builder()
                .id(productId)
                .name("Product Unavailable")
                .price(BigDecimal.ZERO)
                .build();
    }

    @Override
    public Boolean checkProductAvailability(List<ProductAvailabilityRequest> request) {
        // Return false or cached availability
        return false;
    }

    @Override
    public Boolean reserveProducts(List<ProductReservationRequest> request) {
        return false;
    }

    @Override
    public void updateStock(Long productId, StockUpdateRequest request) {
        // Log failure for eventual consistency
        log.error("Failed to update stock for product: {}", productId);
    }
}
