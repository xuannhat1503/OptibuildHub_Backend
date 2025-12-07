package com.optibuildhub.forum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    @NotBlank
    private String title;
    private String content;
    private Long userId;    // tạm truyền từ client
    private Long buildId;   // optional share build
    private List<String> imageUrls; // đã upload trước và nhận URL
}