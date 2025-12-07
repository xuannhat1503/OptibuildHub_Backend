package com.optibuildhub.forum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    private Long userId; // tạm truyền
    @NotBlank
    private String content;
    private Long parentId;
}