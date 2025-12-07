package com.optibuildhub.part;

import com.optibuildhub.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartFinder {
    private final PartRepository partRepo;

    public Part mustFind(Long id) {
        return partRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Part not found: " + id));
    }
}