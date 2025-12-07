package com.optibuildhub.pcbuild;

import com.optibuildhub.part.Part;
import com.optibuildhub.user.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class PcBuildMapper {

    public PcBuild toEntity(User user, String title, List<Part> parts) {
        PcBuild build = PcBuild.builder()
                .user(user)
                .title(title)
                .isShared(false)
                .createdAt(Instant.now())
                .build();

        List<PcBuildItem> items = parts.stream()
                .map(p -> PcBuildItem.builder()
                        .build(build)
                        .part(p)
                        .quantity(1)
                        .build())
                .toList();

        build.setItems(items);
        build.setTotalPrice(parts.stream()
                .map(p -> Optional.ofNullable(p.getPrice()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        build.setWattageTotal(parts.stream()
                .map(p -> Optional.ofNullable(p.getWattage()).orElse(0))
                .reduce(0, Integer::sum));
        return build;
    }
}