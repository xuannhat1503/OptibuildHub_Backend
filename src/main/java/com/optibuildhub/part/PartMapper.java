package com.optibuildhub.part;

import com.optibuildhub.part.dto.PartRequest;
import com.optibuildhub.part.dto.PartResponse;

public class PartMapper {
    public static Part toEntity(PartRequest r) {
        return Part.builder()
                .name(r.getName())
                .category(r.getCategory())
                .brand(r.getBrand())
                .specJson(r.getSpecJson())
                .wattage(r.getWattage())
                .price(r.getPrice())
                .imageUrl(r.getImageUrl())
                .build();
    }

    public static PartResponse toResponse(Part p) {
        return PartResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .category(p.getCategory())
                .brand(p.getBrand())
                .specJson(p.getSpecJson())
                .wattage(p.getWattage())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl())
                .createdAt(p.getCreatedAt())
                .build();
    }
}