package com.optibuildhub.pcbuild;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.pcbuild.dto.*;
import com.optibuildhub.part.PartRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}