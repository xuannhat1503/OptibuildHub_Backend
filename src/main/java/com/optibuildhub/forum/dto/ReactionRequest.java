package com.optibuildhub.forum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactionRequest {
    @NotNull
    private Long userId;
    @NotNull
    private String type; // LIKE / DISLIKE
}