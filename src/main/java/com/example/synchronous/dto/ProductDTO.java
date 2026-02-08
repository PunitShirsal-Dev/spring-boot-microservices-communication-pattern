package com.example.synchronous.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Product code is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Product code must be alphanumeric with hyphens")
    private String code;

    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount cannot exceed 100%")
    private BigDecimal discountPercentage;

    private BigDecimal discountedPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Min(value = 0, message = "Minimum quantity cannot be negative")
    private Integer minQuantity;

    @Min(value = 0, message = "Maximum quantity cannot be negative")
    private Integer maxQuantity;

    @NotBlank(message = "Category is required")
    private String category;

    private List<String> subCategories;

    @NotBlank(message = "Brand is required")
    private String brand;

    private String sku;
    private String upc;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    private List<String> tags;

    private Double rating;
    private Integer reviewCount;
    private Integer soldCount;

    private Boolean active;
    private Boolean featured;
    private Boolean inStock;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    // Metadata
    private Map<String, Object> metadata;

    // Audit fields
    private String version;
    private Long revision;
}
