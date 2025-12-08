package com.optibuildhub.part;

import com.optibuildhub.part.dto.PartFilter;
import com.optibuildhub.rating.RatingRepository;
import com.optibuildhub.pcbuild.PcBuildItemRepository;
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
    private final RatingRepository ratingRepo;
    private final PcBuildItemRepository buildItemRepo;

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

    @Transactional
    public void delete(Long id) {
        // Check if part exists
        Part part = partRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Part not found"));
        
        // Delete all related data first
        // 1. Delete ratings
        ratingRepo.deleteByPartId(id);
        
        // 2. Delete price history
        priceHistoryRepo.deleteByPartId(id);
        
        // 3. Remove part from all builds (delete PcBuildItem entries)
        buildItemRepo.deleteByPartId(id);
        
        // 4. Finally delete the part
        partRepo.deleteById(id);
    }

    public java.util.List<PartPriceHistory> getHistory(Long partId) {
        return priceHistoryRepo.findTop30ByPartIdOrderByCrawledAtDesc(partId);
    }
    
    public java.util.List<PartPriceHistory> getRecentHistory(Long partId) {
        return priceHistoryRepo.findTop7ByPartIdOrderByCrawledAtDesc(partId);
    }
}