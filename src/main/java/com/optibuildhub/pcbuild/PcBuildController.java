package com.optibuildhub.pcbuild;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.pcbuild.dto.*;
import com.optibuildhub.part.PartRepository;
import com.optibuildhub.part.PartMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
public class PcBuildController {

    private final PcBuildService buildService;
    private final PartRepository partRepo;

    @PostMapping("/check")
    public ApiResponse<BuildCheckResponse> check(@Valid @RequestBody BuildCheckRequest req) {
        var rs = buildService.checkCompatibility(req.getPartIds());
        return ApiResponse.ok(BuildCheckResponse.builder()
                .compatible(rs.isCompatible())
                .warnings(rs.getWarnings())
                .totalPrice(rs.getTotalPrice())
                .totalWatt(rs.getTotalWatt())
                .build());
    }

    @PostMapping
    public ApiResponse<BuildResponse> save(@Valid @RequestBody BuildSaveRequest req) {
        var build = buildService.saveBuild(req.getUserId(), req.getTitle(), req.getPartIds());
        var partIds = build.getItems().stream().map(i -> i.getPart().getId()).collect(Collectors.toList());
        return ApiResponse.ok(BuildResponse.builder()
                .id(build.getId())
                .title(build.getTitle())
                .totalPrice(build.getTotalPrice())
                .wattageTotal(build.getWattageTotal())
                .shared(Boolean.TRUE.equals(build.getIsShared()))
                .partIds(partIds)
                .build());
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<BuildResponse>> getUserBuilds(@PathVariable Long userId) {
        var builds = buildService.getUserBuilds(userId);
        var responses = builds.stream()
                .map(build -> {
                    var partIds = build.getItems().stream()
                            .map(i -> i.getPart().getId())
                            .collect(Collectors.toList());
                    return BuildResponse.builder()
                            .id(build.getId())
                            .title(build.getTitle())
                            .totalPrice(build.getTotalPrice())
                            .wattageTotal(build.getWattageTotal())
                            .shared(Boolean.TRUE.equals(build.getIsShared()))
                            .partIds(partIds)
                            .build();
                })
                .collect(Collectors.toList());
        return ApiResponse.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        buildService.deleteBuild(id);
        return ApiResponse.ok("Build deleted successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<BuildDetailResponse> getDetail(@PathVariable Long id) {
        var build = buildService.getBuild(id);
        var parts = build.getItems().stream()
                .map(i -> PartMapper.toResponse(i.getPart(), null))
                .collect(Collectors.toList());
        
        return ApiResponse.ok(BuildDetailResponse.builder()
                .id(build.getId())
                .title(build.getTitle())
                .totalPrice(build.getTotalPrice())
                .wattageTotal(build.getWattageTotal())
                .shared(Boolean.TRUE.equals(build.getIsShared()))
                .parts(parts)
                .build());
    }
}