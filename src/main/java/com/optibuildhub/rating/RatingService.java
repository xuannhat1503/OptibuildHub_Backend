package com.optibuildhub.rating;

import com.optibuildhub.part.Part;
import com.optibuildhub.part.PartRepository;
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

    public List<Rating> listByPart(Long partId) {
        return ratingRepository.findByPartId(partId);
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
                .orElseGet(Rating::new);
        r.setPart(part);
        r.setUser(user);
        r.setScore(score);
        r.setContent(content);
        return ratingRepository.save(r);
    }
}