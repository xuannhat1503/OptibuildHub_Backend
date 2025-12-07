package com.optibuildhub.forum;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.forum.dto.PostDetailView;
import com.optibuildhub.forum.ReactionRepository; // ⬅️ chỉnh lại đúng package của bạn nếu khác
import com.optibuildhub.forum.ReactionType;       // ⬅️ chỉnh lại đúng package của bạn nếu khác
import com.optibuildhub.forum.CommentRepository;   // ⬅️ chỉnh lại đúng package của bạn nếu khác
import com.optibuildhub.forum.dto.PostRequest;
import com.optibuildhub.forum.dto.PostView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ForumController {

    private final PostService postService;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    @GetMapping
    public ApiResponse<Page<PostView>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postService.list(pageable);

        Page<PostView> views = posts.map(p -> {
            long likeCnt = reactionRepository.countByPostIdAndType(p.getId(), ReactionType.LIKE);
            long dislikeCnt = reactionRepository.countByPostIdAndType(p.getId(), ReactionType.DISLIKE);
            long commentCnt = commentRepository.countByPostId(p.getId());
            return PostViewMapper.toView(p, likeCnt, dislikeCnt, commentCnt);
        });

        return ApiResponse.ok(views);
    }

    @PostMapping
    public ApiResponse<PostView> createPost(@Valid @RequestBody PostRequest req) {
        Post post = postMapper.toPost(req);
        Post saved = postService.create(post, req.getImageUrls());

        long likeCnt = 0;
        long dislikeCnt = 0;
        long commentCnt = 0;

        return ApiResponse.ok(PostViewMapper.toView(saved, likeCnt, dislikeCnt, commentCnt));
    }
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