package com.optibuildhub.rating;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.rating.dto.RatingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parts/{partId}/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public ApiResponse<?> list(@PathVariable Long partId) {
        return ApiResponse.ok(ratingService.listByPart(partId));
    }

    @PostMapping
    public ApiResponse<?> upsert(@PathVariable Long partId, @Valid @RequestBody RatingRequest req) {
        return ApiResponse.ok(ratingService.upsert(partId, req.getUserId(), req.getScore(), req.getContent()));
    }
}