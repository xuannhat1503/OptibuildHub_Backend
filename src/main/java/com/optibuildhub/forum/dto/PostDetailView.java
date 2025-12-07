package com.optibuildhub.forum.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data @Builder
public class PostDetailView {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;
    private Long buildId;
    private Instant createdAt;

    private long likeCount;
    private long dislikeCount;
    private long commentCount;

    private List<String> imageUrls;
    private List<CommentView> comments;

    @Data @Builder
    public static class CommentView {
        private Long id;
        private Long userId;
        private String userName;
        private String content;
        private Long parentId;
        private Instant createdAt;
    }
}