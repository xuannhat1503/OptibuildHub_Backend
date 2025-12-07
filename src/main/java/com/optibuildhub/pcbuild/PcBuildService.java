package com.optibuildhub.pcbuild;

import com.optibuildhub.part.PartRepository;
import com.optibuildhub.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PcBuildService {

    private final PartRepository partRepo;
    private final PcBuildRepository buildRepo;
    private final PcBuildMapper buildMapper;
    private final CompatibilityRuleService ruleService;
    private final UserFinder userFinder;

    public CompatibilityResult checkCompatibility(List<Long> partIds) {
        var parts = partRepo.findAllById(partIds);
        return ruleService.check(parts);
    }

    @Transactional
    public PcBuild saveBuild(Long userId, String title, List<Long> partIds) {
        var user = userFinder.mustFind(userId);
        var parts = partRepo.findAllById(partIds);
        var build = buildMapper.toEntity(user, title, parts);
        return buildRepo.save(build);
    }
}