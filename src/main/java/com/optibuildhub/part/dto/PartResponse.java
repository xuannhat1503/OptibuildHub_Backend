package com.optibuildhub.part.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data @Builder
public class PartResponse {
    private Long id;
    private String name;
    private String category;
    private String brand;
    private String specJson;
    private Integer wattage;
    private BigDecimal price;
    private String imageUrl;
    private Instant createdAt;
    private Double avgRating;
    private Long ratingCount;
    private List<PricePoint> priceHistory;
    
    @Data @Builder
    public static class PricePoint {
        private Instant crawledAt;
        private BigDecimal price;
        private String source;
    }
}