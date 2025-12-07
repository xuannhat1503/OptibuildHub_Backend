package com.optibuildhub.pcbuild.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class BuildResponse {
    private Long id;
    private String title;
    private BigDecimal totalPrice;
    private int wattageTotal;
    private boolean shared;
    private List<Long> partIds;
}