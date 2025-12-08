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

    // Chạy mỗi 6 giờ
    @Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void crawlAll() {
        log.info("Start crawling prices...");
        // Chỉ cào những part có crawlUrl
        var parts = partRepo.findAll().stream()
            .filter(p -> p.getCrawlUrl() != null && !p.getCrawlUrl().isBlank())
            .toList();
            
        log.info("Found {} parts with crawl URLs", parts.size());
        
        int success = 0;
        for (Part p : parts) {
            try {
                BigDecimal price = crawlPriceFromSource(p);
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    String source = detectSource(p.getCrawlUrl());
                    updatePrice(p, price, source);
                    success++;
                    // throttle nhẹ để tránh bị chặn
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                log.warn("Crawl failed for part {}: {}", p.getId(), e.getMessage());
            }
        }
        log.info("Crawl done. Success: {}/{}", success, parts.size());
    }
    
    private String detectSource(String url) {
        if (url == null) return "unknown";
        if (url.contains("gearvn.com")) return "gearvn";
        if (url.contains("phongvu.vn")) return "phongvu";
        if (url.contains("tgdd.vn")) return "tgdd";
        return "other";
    }

    /**
     * Cào giá cho một Part từ nguồn (ví dụ gearvn). Bạn cần chỉnh URL & selector.
     */
    public BigDecimal crawlPriceFromSource(Part part) throws Exception {
        // Sử dụng crawlUrl từ Part entity
        String sourceUrl = part.getCrawlUrl();
        if (sourceUrl == null || sourceUrl.isBlank()) {
            return null;
        }
        
        log.debug("Crawling price from: {}", sourceUrl);
        
        Document doc = Jsoup.connect(sourceUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .timeout(15_000)
                .followRedirects(true)
                .get();

        // Selectors for different websites
        // GearVN: .pro-price (giá khuyến mãi), .product-price, span[itemprop="price"]
        // Phong Vũ: .product-price__sale-price
        // Ưu tiên lấy giá khuyến mãi trước, nếu không có thì lấy giá gốc
        Element priceEl = doc.selectFirst(
            ".pro-price, .special-price .price, .product-price__sale-price, " +
            ".price--final, span[itemprop='price'], .product-price, .product-sale-price"
        );
        
        if (priceEl == null) {
            log.warn("Could not find price element for URL: {}", sourceUrl);
            return null;
        }

        String raw = priceEl.text();
        log.debug("Found price text: {}", raw);
        BigDecimal price = parsePrice(raw);
        
        if (price != null) {
            log.info("Successfully crawled price {} for part {}", price, part.getName());
        }
        
        return price;
    }

    private String extractSourceUrl(Part part) {
        // Deprecated: now using part.getCrawlUrl() directly
        return part.getCrawlUrl();
    }

    private BigDecimal parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return null;
        
        // Remove common currency symbols and text
        String cleaned = raw.toLowerCase()
            .replace("đ", "")
            .replace("₫", "")
            .replace("vnd", "")
            .replace("vnđ", "")
            .trim();
        
        // Split by whitespace and take first number (in case there are multiple prices)
        String[] parts = cleaned.split("\\s+");
        String firstPrice = null;
        for (String part : parts) {
            // Remove all non-digit characters except decimal point
            String digits = part.replaceAll("[^0-9.]", "");
            if (!digits.isBlank()) {
                firstPrice = digits;
                break;
            }
        }
        
        if (firstPrice == null || firstPrice.isBlank()) {
            log.warn("Could not parse price from: {}", raw);
            return null;
        }
        
        // Remove decimal point if exists (prices in VND don't use decimals)
        firstPrice = firstPrice.replace(".", "");
        
        try {
            return new BigDecimal(firstPrice);
        } catch (NumberFormatException e) {
            log.error("Failed to parse price: {}", raw, e);
            return null;
        }
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
    
    /**
     * Crawl and update price for a single part (for manual trigger)
     */
    public BigDecimal crawlAndUpdatePrice(Part part) throws Exception {
        BigDecimal price = crawlPriceFromSource(part);
        if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            String source = detectSource(part.getCrawlUrl());
            updatePrice(part, price, source);
        }
        return price;
    }
}