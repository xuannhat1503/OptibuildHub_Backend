package com.optibuildhub.forum;

import com.optibuildhub.common.NotFoundException;
import com.optibuildhub.forum.dto.CommentRequest;
import com.optibuildhub.forum.dto.PostRequest;
import com.optibuildhub.forum.dto.ReactionRequest;
import com.optibuildhub.pcbuild.PcBuild;
import com.optibuildhub.pcbuild.PcBuildRepository;
import com.optibuildhub.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final UserFinder userFinder;
    private final PcBuildRepository buildRepo;

    public Post toPost(PostRequest req) {
        var user = userFinder.mustFind(req.getUserId());
        PcBuild build = null;
        if (req.getBuildId() != null) {
            build = buildRepo.findById(req.getBuildId())
                    .orElseThrow(() -> new NotFoundException("Build not found: " + req.getBuildId()));
        }
        Post post = Post.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .build(build)
                .createdAt(Instant.now())
                .build();
        if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
            List<PostImage> images = req.getImageUrls().stream()
                    .map(url -> PostImage.builder().post(post).url(url).build())
                    .toList();
            post.setImages(images);
        }
        return post;
    }

    public Comment toComment(Long postId, CommentRequest req, Post post) {
        var user = userFinder.mustFind(req.getUserId());
        Comment parent = null;
        if (req.getParentId() != null) {
            parent = Comment.builder().id(req.getParentId()).build(); // hoặc fetch nếu cần kiểm tra tồn tại
        }
        return Comment.builder()
                .post(post)
                .user(user)
                .content(req.getContent())
                .parent(parent)
                .createdAt(Instant.now())
                .build();
    }

    public Reaction toReaction(Long postId, ReactionRequest req, Post post) {
        var user = userFinder.mustFind(req.getUserId());
        var type = ReactionType.valueOf(req.getType().toUpperCase());
        return Reaction.builder()
                .post(post)
                .user(user)
                .type(type)
                .createdAt(Instant.now())
                .build();
    }
}