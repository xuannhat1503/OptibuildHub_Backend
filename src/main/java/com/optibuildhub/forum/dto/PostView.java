package com.optibuildhub.forum.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data @Builder
public class PostView {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;   // nếu có
    private Long buildId;      // nếu share build
    private Instant createdAt;

    private long likeCount;
    private long dislikeCount;
    private long commentCount;

    private List<String> imageUrls;
}