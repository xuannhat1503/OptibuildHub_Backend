package com.optibuildhub.rating.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class RatingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String userRole;
    private Integer score;
    private String content;
    private Instant createdAt;
}
