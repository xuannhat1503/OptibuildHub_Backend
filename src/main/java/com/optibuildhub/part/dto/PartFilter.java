package com.optibuildhub.part.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PartFilter {
    private String category;
    private String brand;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String keyword; // tìm theo name/brand
}