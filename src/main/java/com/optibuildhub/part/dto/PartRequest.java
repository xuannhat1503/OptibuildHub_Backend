package com.optibuildhub.part.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String category; // CPU, GPU, RAM, PSU...

    private String brand;

    private String specJson; // optional JSON string

    @Min(0)
    private Integer wattage;

    @NotNull @DecimalMin("0.0")
    private BigDecimal price;

    private String imageUrl;
}