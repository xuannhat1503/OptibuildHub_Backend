package com.optibuildhub.rating;

import com.optibuildhub.part.PartFinder;
import com.optibuildhub.user.UserFinder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class RatingMapper {

    private final PartFinder partFinder;
    private final UserFinder userFinder;

    public Rating toNew(Long partId, Long userId, int score, String content) {
        return Rating.builder()
                .part(partFinder.mustFind(partId))
                .user(userFinder.mustFind(userId))
                .score(score)
                .content(content)
                .createdAt(Instant.now())
                .build();
    }
}