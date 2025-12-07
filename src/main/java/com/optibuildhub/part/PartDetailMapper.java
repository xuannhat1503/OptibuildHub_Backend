package com.optibuildhub.part;

import com.optibuildhub.part.dto.PartDetailResponse;
import java.util.List;

public class PartDetailMapper {
    public static PartDetailResponse toResponse(Part p,
                                                Double ratingAvg,
                                                Long ratingCount,
                                                List<PartPriceHistory> history) {
        return PartDetailResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .brand(p.getBrand())
                .specJson(p.getSpecJson())
                .wattage(p.getWattage())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl())
                .createdAt(p.getCreatedAt())
                .ratingAvg(ratingAvg)
                .ratingCount(ratingCount)
                .priceHistory(history.stream()
                        .map(h -> PartDetailResponse.PricePoint.builder()
                                .crawledAt(h.getCrawledAt())
                                .price(h.getPrice())
                                .source(h.getSource())
                                .build())
                        .toList())
                .build();
    }
}