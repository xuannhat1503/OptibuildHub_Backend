package com.optibuildhub.part;

import com.optibuildhub.part.dto.PartFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartService {
    private final PartRepository partRepo;
    private final PartPriceHistoryRepository priceHistoryRepo;

    public Page<Part> list(PartFilter filter, Pageable pageable) {
        var spec = PartSpecifications.filter(filter);
        return partRepo.findAll(spec, pageable);
    }

    public Part get(Long id) {
        return partRepo.findById(id).orElseThrow();
    }

    @Transactional
    public Part createOrUpdate(Part p) {
        return partRepo.save(p);
    }

    public java.util.List<PartPriceHistory> getHistory(Long partId) {
        return priceHistoryRepo.findTop30ByPartIdOrderByCrawledAtDesc(partId);
    }
}