package com.optibuildhub.part.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

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
}