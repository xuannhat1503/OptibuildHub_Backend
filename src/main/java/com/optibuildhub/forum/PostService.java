package com.optibuildhub.forum;

import com.optibuildhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepo;
    private final CommentRepository commentRepo;
    private final ReactionRepository reactionRepo;
    private final UserRepository userRepo;

    public Page<Post> list(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    public Post detail(Long id) {
        return postRepo.findById(id).orElseThrow();
    }

    @Transactional
    public Post create(Post post, List<String> imageUrls) {
        post.setCreatedAt(Instant.now());
        if (imageUrls != null && !imageUrls.isEmpty()) {
            post.setImages(
                    imageUrls.stream()
                            .map(url -> PostImage.builder().post(post).url(url).build())
                            .collect(java.util.stream.Collectors.toCollection(ArrayList::new))
            );
        }
        return postRepo.save(post);
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
                        .user(userRepo.getReferenceById(userId))
                        .type(type)
                        .createdAt(Instant.now())
                        .build());
        return reactionRepo.save(r);
    }

    @Transactional
    public Post update(Post post, List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            post.setImages(
                    imageUrls.stream()
                            .map(url -> PostImage.builder().post(post).url(url).build())
                            .toList()
            );
        }
        return postRepo.save(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepo.findById(id).orElseThrow();
        
        // Get all comments for this post
        List<Comment> comments = commentRepo.findByPostIdOrderByCreatedAtAsc(id);
        
        // Delete child comments first (where parent is not null)
        comments.stream()
            .filter(c -> c.getParent() != null)
            .forEach(commentRepo::delete);
        
        // Delete parent comments
        comments.stream()
            .filter(c -> c.getParent() == null)
            .forEach(commentRepo::delete);
        
        // Delete reactions
        reactionRepo.deleteByPostId(id);
        
        // Delete post (images will be cascade deleted)
        postRepo.delete(post);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        // Delete all replies first
        commentRepo.deleteByParentId(commentId);
        commentRepo.delete(comment);
    }

    @Transactional
    public void removeReaction(Long postId, Long userId) {
        reactionRepo.findByPostIdAndUserId(postId, userId)
                .ifPresent(reactionRepo::delete);
    }

    public Page<Post> getPostsByUserId(Long userId, Pageable pageable) {
        return postRepo.findByUserId(userId, pageable);
    }

    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                keyword, keyword, pageable);
    }
}