package com.optibuildhub.pcbuild.dto;

import com.optibuildhub.part.dto.PartResponse;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder
public class BuildDetailResponse {
    private Long id;
    private String title;
    private BigDecimal totalPrice;
    private int wattageTotal;
    private boolean shared;
    private List<PartResponse> parts;
}
