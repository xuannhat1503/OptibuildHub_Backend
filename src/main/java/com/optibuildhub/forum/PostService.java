package com.optibuildhub.forum;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final ReactionRepository reactionRepo;

    public Page<Post> list(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    public Post detail(Long id) {
        return postRepo.findById(id).orElseThrow();
    }

    @Transactional
    public Post create(Post post, List<String> imageUrls) {
        post.setCreatedAt(Instant.now());
        Post saved = postRepo.save(post);
        if (imageUrls != null && !imageUrls.isEmpty()) {
            saved.setImages(
                    imageUrls.stream()
                            .map(url -> PostImage.builder().post(saved).url(url).build())
                            .toList()
            );
        }
        return postRepo.save(saved);
    }

    @Transactional
    public Comment addComment(Long postId, Comment c) {
        Post p = postRepo.findById(postId).orElseThrow();
        c.setPost(p);
        c.setCreatedAt(Instant.now());
        return commentRepo.save(c);
    }

    @Transactional
    public Reaction react(Long postId, Long userId, ReactionType type) {
        Reaction r = reactionRepo.findByPostIdAndUserId(postId, userId)
                .map(existing -> { existing.setType(type); return existing; })
                .orElseGet(() -> Reaction.builder()
                        .post(postRepo.getReferenceById(postId))
                        .user(null) // set user entity nếu cần
                        .type(type)
                        .createdAt(Instant.now())
                        .build());
        return reactionRepo.save(r);
    }
}