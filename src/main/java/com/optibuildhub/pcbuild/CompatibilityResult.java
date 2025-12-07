package com.optibuildhub.pcbuild;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CompatibilityResult {
    private boolean compatible;
    private List<String> warnings;
    private BigDecimal totalPrice;
    private int totalWatt;          // tổng (cpuTdp + gpuTdp + các wattage khác)
}