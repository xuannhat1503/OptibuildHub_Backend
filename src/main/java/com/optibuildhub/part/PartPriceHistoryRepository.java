package com.optibuildhub.part;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartPriceHistoryRepository extends JpaRepository<PartPriceHistory, Long> {

    // Lấy tất cả theo partId
    List<PartPriceHistory> findByPartId(Long partId);

    // Hoặc giới hạn/ordering: top 30 mới nhất
    List<PartPriceHistory> findTop30ByPartIdOrderByCrawledAtDesc(Long partId);
}