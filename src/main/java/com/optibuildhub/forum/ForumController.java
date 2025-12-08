package com.optibuildhub.forum;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.forum.dto.PostDetailView;
import com.optibuildhub.forum.dto.PostRequest;
import com.optibuildhub.forum.dto.PostView;
import com.optibuildhub.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class ForumController {

    private final PostService postService;
    private final ReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;
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
        try {
            System.out.println("Creating post with userId: " + req.getUserId() + ", title: " + req.getTitle());
            Post post = postMapper.toPost(req);
            Post saved = postService.create(post, req.getImageUrls());

            long likeCnt = 0;
            long dislikeCnt = 0;
            long commentCnt = 0;

            return ApiResponse.ok(PostViewMapper.toView(saved, likeCnt, dislikeCnt, commentCnt));
        } catch (Exception e) {
            System.err.println("Error creating post: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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

    @PostMapping("/{postId}/comments")
    public ApiResponse<String> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> body
    ) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String content = body.get("content").toString();
        Long parentId = body.get("parentId") != null ? Long.valueOf(body.get("parentId").toString()) : null;

        Comment comment = Comment.builder()
                .user(userRepository.getReferenceById(userId))
                .content(content)
                .parent(parentId != null ? commentRepository.getReferenceById(parentId) : null)
                .build();

        postService.addComment(postId, comment);
        return ApiResponse.ok("Comment added");
    }

    @PostMapping("/{postId}/reactions")
    public ApiResponse<String> addReaction(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> body
    ) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String typeStr = body.get("type").toString();
        ReactionType type = ReactionType.valueOf(typeStr.toUpperCase());

        postService.react(postId, userId, type);
        return ApiResponse.ok("Reaction added");
    }

    @PutMapping("/{id}")
    public ApiResponse<PostView> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest req
    ) {
        Post existing = postService.detail(id);
        
        // Update fields
        if (req.getTitle() != null) existing.setTitle(req.getTitle());
        if (req.getContent() != null) existing.setContent(req.getContent());
        
        Post updated = postService.update(existing, req.getImageUrls());
        
        long likeCnt = reactionRepository.countByPostIdAndType(id, ReactionType.LIKE);
        long dislikeCnt = reactionRepository.countByPostIdAndType(id, ReactionType.DISLIKE);
        long commentCnt = commentRepository.countByPostId(id);

        return ApiResponse.ok(PostViewMapper.toView(updated, likeCnt, dislikeCnt, commentCnt));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ApiResponse.ok("Post deleted");
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ApiResponse<String> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postService.deleteComment(commentId);
        return ApiResponse.ok("Comment deleted");
    }

    @DeleteMapping("/{postId}/reactions")
    public ApiResponse<String> removeReaction(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        postService.removeReaction(postId, userId);
        return ApiResponse.ok("Reaction removed");
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Page<PostView>> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postService.getPostsByUserId(userId, pageable);

        Page<PostView> views = posts.map(p -> {
            long likeCnt = reactionRepository.countByPostIdAndType(p.getId(), ReactionType.LIKE);
            long dislikeCnt = reactionRepository.countByPostIdAndType(p.getId(), ReactionType.DISLIKE);
            long commentCnt = commentRepository.countByPostId(p.getId());
            return PostViewMapper.toView(p, likeCnt, dislikeCnt, commentCnt);
        });

        return ApiResponse.ok(views);
    }

    @GetMapping("/search")
    public ApiResponse<Page<PostView>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postService.searchPosts(keyword, pageable);

        Page<PostView> views = posts.map(p -> {
            long likeCnt = reactionRepository.countByPostIdAndType(p.getId(), ReactionType.LIKE);
            long dislikeCnt = reactionRepository.countByPostIdAndType(p.getId(), ReactionType.DISLIKE);
            long commentCnt = commentRepository.countByPostId(p.getId());
            return PostViewMapper.toView(p, likeCnt, dislikeCnt, commentCnt);
        });

        return ApiResponse.ok(views);
    }
}