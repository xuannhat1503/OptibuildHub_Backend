package com.optibuildhub.pcbuild;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartSpecParser {
    private final ObjectMapper objectMapper;

    public PartSpec parse(String specJson) {
        if (specJson == null || specJson.isBlank()) return new PartSpec();
        try {
            return objectMapper.readValue(specJson, PartSpec.class);
        } catch (Exception e) {
            // Nếu parse lỗi, trả spec rỗng để tránh crash
            return new PartSpec();
        }
    }
}