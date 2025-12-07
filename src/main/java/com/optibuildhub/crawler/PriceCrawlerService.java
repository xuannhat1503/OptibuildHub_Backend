package com.optibuildhub.crawler;

import com.optibuildhub.part.Part;
import com.optibuildhub.part.PartPriceHistory;
import com.optibuildhub.part.PartPriceHistoryRepository;
import com.optibuildhub.part.PartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceCrawlerService {

    private final PartRepository partRepo;
    private final PartPriceHistoryRepository historyRepo;

    // Chạy mỗi 4 giờ
    @Scheduled(fixedDelay = 4, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void crawlAll() {
        log.info("Start crawling prices...");
        // Ví dụ: bạn có thể duyệt tất cả Part có category trong nhóm cần cào
        var parts = partRepo.findAll();
        int success = 0;
        for (Part p : parts) {
            try {
                BigDecimal price = crawlPriceFromSource(p);
                if (price != null) {
                    updatePrice(p, price, "gearvn"); // source ví dụ
                    success++;
                    // throttle nhẹ để tránh bị chặn
                    Thread.sleep(300);
                }
            } catch (Exception e) {
                log.warn("Crawl failed for part {}: {}", p.getId(), e.getMessage());
            }
        }
        log.info("Crawl done. Success: {}", success);
    }

    /**
     * Cào giá cho một Part từ nguồn (ví dụ gearvn). Bạn cần chỉnh URL & selector.
     */
    public BigDecimal crawlPriceFromSource(Part part) throws Exception {
        // Giả định part.specJson có chứa "sourceUrl"
        String sourceUrl = extractSourceUrl(part);
        if (sourceUrl == null || sourceUrl.isBlank()) {
            return null;
        }
        Document doc = Jsoup.connect(sourceUrl)
                .userAgent("Mozilla/5.0 (compatible; optibuild-crawler/1.0)")
                .timeout(10_000)
                .get();

        // TODO: chỉnh selector phù hợp trang. Ví dụ giá hiển thị tại <span class="product-sale-price">
        Element priceEl = doc.selectFirst(".product-sale-price, .product-price, .price--final");
        if (priceEl == null) return null;

        String raw = priceEl.text(); // ví dụ "12.490.000đ"
        BigDecimal price = parsePrice(raw);
        return price;
    }

    private String extractSourceUrl(Part part) {
        // Nếu bạn lưu link trong specJson hoặc trường riêng, lấy ra ở đây.
        // Ví dụ specJson có key "url": {"url":"https://example.com/..."}
        // Tạm bỏ trống: return null;
        return null;
    }

    private BigDecimal parsePrice(String raw) {
        if (raw == null) return null;
        // Loại bỏ ký tự không phải số
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isBlank()) return null;
        return new BigDecimal(digits);
    }

    private void updatePrice(Part part, BigDecimal newPrice, String source) {
        // Nếu giá không đổi, bạn có thể bỏ qua history; ở đây vẫn ghi history
        part.setPrice(newPrice);
        partRepo.save(part);

        PartPriceHistory hist = PartPriceHistory.builder()
                .part(part)
                .price(newPrice)
                .source(source)
                .crawledAt(Instant.now())
                .build();
        historyRepo.save(hist);
    }
}