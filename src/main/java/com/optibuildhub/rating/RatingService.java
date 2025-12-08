package com.optibuildhub.rating;

import com.optibuildhub.part.Part;
import com.optibuildhub.part.PartRepository;
import com.optibuildhub.rating.dto.RatingResponse;
import com.optibuildhub.user.User;
import com.optibuildhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    public List<RatingResponse> listByPart(Long partId) {
        return ratingRepository.findByPartId(partId).stream()
                .map(r -> RatingResponse.builder()
                        .id(r.getId())
                        .userId(r.getUser() != null ? r.getUser().getId() : null)
                        .userName(r.getUser() != null ? r.getUser().getFullName() : "Anonymous")
                        .userAvatar(r.getUser() != null ? r.getUser().getAvatarUrl() : null)
                        .score(r.getScore())
                        .content(r.getContent())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }

    public Double avgByPart(Long partId) {
        Double avg = ratingRepository.averageScore(partId);
        return avg == null ? 0.0 : avg;
    }

    public Long countByPart(Long partId) {
        return ratingRepository.countByPartId(partId);
    }

    public Rating upsert(Long partId, Long userId, Integer score, String content) {
        Part part = partRepository.findById(partId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        Rating r = ratingRepository.findByPartIdAndUserId(partId, userId)
                .orElseGet(() -> {
                    Rating newRating = new Rating();
                    newRating.setCreatedAt(java.time.Instant.now());
                    return newRating;
                });
        r.setPart(part);
        r.setUser(user);
        r.setScore(score);
        r.setContent(content);
        return ratingRepository.save(r);
    }
}