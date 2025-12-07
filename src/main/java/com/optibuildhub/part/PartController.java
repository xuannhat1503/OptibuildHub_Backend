package com.optibuildhub.part;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.part.dto.PartDetailResponse;
import com.optibuildhub.part.dto.PartFilter;
import com.optibuildhub.part.dto.PartRequest;
import com.optibuildhub.part.dto.PartResponse;
import com.optibuildhub.rating.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;
    private final RatingService ratingService; // giả định bạn đã có service này

    // Danh sách + lọc
    @GetMapping
    public ApiResponse<Page<PartResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        PartFilter f = new PartFilter();
        f.setCategory(category);
        f.setBrand(brand);
        f.setMinPrice(minPrice);
        f.setMaxPrice(maxPrice);
        f.setKeyword(q);

        String sortField = allowSort(sortBy) ? sortBy : "createdAt";
        Sort.Direction dir = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));

        var pageResult = partService.list(f, pageable).map(PartMapper::toResponse);
        return ApiResponse.ok(pageResult);
    }

    private boolean allowSort(String field) {
        return "name".equalsIgnoreCase(field)
                || "price".equalsIgnoreCase(field)
                || "createdAt".equalsIgnoreCase(field);
    }

    // Chi tiết part (kèm rating + price history top 30)
    @GetMapping("/{id}")
    public ApiResponse<PartDetailResponse> getPartDetail(@PathVariable Long id) {
        Part p = partService.get(id);
        Double avg = ratingService.avgByPart(id);
        Long cnt = ratingService.countByPart(id);
        var hist = partService.getHistory(id); // top 30 desc
        return ApiResponse.ok(PartDetailMapper.toResponse(p, avg, cnt, hist));
    }

    // Lịch sử giá riêng (để vẽ chart)
    @GetMapping("/{id}/prices")
    public ApiResponse<List<PartDetailResponse.PricePoint>> getPriceHistory(@PathVariable Long id) {
        var hist = partService.getHistory(id);
        var points = hist.stream()
                .map(h -> PartDetailResponse.PricePoint.builder()
                        .crawledAt(h.getCrawledAt())
                        .price(h.getPrice())
                        .source(h.getSource())
                        .build())
                .toList();
        return ApiResponse.ok(points);
    }

    // Tạo mới
    @PostMapping
    public ApiResponse<PartResponse> create(@RequestBody PartRequest req) {
        var saved = partService.createOrUpdate(PartMapper.toEntity(req));
        return ApiResponse.ok(PartMapper.toResponse(saved));
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ApiResponse<PartResponse> update(@PathVariable Long id, @RequestBody PartRequest req) {
        var part = PartMapper.toEntity(req);
        part.setId(id);
        var saved = partService.createOrUpdate(part);
        return ApiResponse.ok(PartMapper.toResponse(saved));
    }
}