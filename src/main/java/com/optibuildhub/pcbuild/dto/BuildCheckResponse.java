package com.optibuildhub.pcbuild.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class BuildCheckResponse {
    private boolean compatible;
    private List<String> warnings;
    private BigDecimal totalPrice;
    private int totalWatt;
}