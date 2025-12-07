package com.optibuildhub.forum;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.forum.dto.PostDetailView;
import com.optibuildhub.forum.ReactionRepository; // ⬅️ chỉnh lại đúng package của bạn nếu khác
import com.optibuildhub.forum.ReactionType;       // ⬅️ chỉnh lại đúng package của bạn nếu khác
import com.optibuildhub.forum.CommentRepository;   // ⬅️ chỉnh lại đúng package của bạn nếu khác
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ForumController {

    private final PostService postService;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;

    @GetMapping("/{id}")
    public ApiResponse<PostDetailView> detail(@PathVariable Long id) {
        Post p = postService.detail(id);
        long likeCnt = reactionRepository.countByPostIdAndType(id, ReactionType.LIKE);
        long dislikeCnt = reactionRepository.countByPostIdAndType(id, ReactionType.DISLIKE);
        long commentCnt = commentRepository.countByPostId(id);

        var comments = commentRepository.findByPostIdOrderByCreatedAtAsc(id).stream()
                .map(c -> PostDetailView.CommentView.builder()
                        .id(c.getId())
                        .userId(c.getUser() != null ? c.getUser().getId() : null)
                        // Nếu User không có getName(), dùng getter bạn có (vd. getEmail())
                        .userName(c.getUser() != null ? c.getUser().getEmail() : null)
                        .content(c.getContent())
                        .parentId(c.getParent() != null ? c.getParent().getId() : null)
                        .createdAt(c.getCreatedAt())
                        .build())
                .toList();

        return ApiResponse.ok(PostViewMapper.toDetail(p, likeCnt, dislikeCnt, commentCnt, comments));
    }
}