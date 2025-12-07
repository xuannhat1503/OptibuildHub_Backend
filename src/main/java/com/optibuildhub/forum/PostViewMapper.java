package com.optibuildhub.forum;

import com.optibuildhub.forum.dto.PostDetailView;
import com.optibuildhub.forum.dto.PostView;

import java.util.List;

public class PostViewMapper {

    private static String userNameSafely(com.optibuildhub.user.User u) {
        if (u == null) return null;
        return u.getEmail(); // đổi sang getter thực tế bạn có, ví dụ getFullName()
    }

    public static PostView toView(Post p, long likeCnt, long dislikeCnt, long commentCnt) {
        return PostView.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .userName(userNameSafely(p.getUser()))
                .buildId(p.getBuild() != null ? p.getBuild().getId() : null)
                .createdAt(p.getCreatedAt())
                .likeCount(likeCnt)
                .dislikeCount(dislikeCnt)
                .commentCount(commentCnt)
                .imageUrls(p.getImages() == null ? List.of() :
                        p.getImages().stream().map(PostImage::getUrl).toList())
                .build();
    }

    public static PostDetailView toDetail(Post p,
                                          long likeCnt,
                                          long dislikeCnt,
                                          long commentCnt,
                                          List<PostDetailView.CommentView> comments) {
        return PostDetailView.builder()
                .id(p.getId())
                .title(p.getTitle())
                .content(p.getContent())
                .userId(p.getUser() != null ? p.getUser().getId() : null)
                .userName(userNameSafely(p.getUser()))
                .buildId(p.getBuild() != null ? p.getBuild().getId() : null)
                .createdAt(p.getCreatedAt())
                .likeCount(likeCnt)
                .dislikeCount(dislikeCnt)
                .commentCount(commentCnt)
                .imageUrls(p.getImages() == null ? List.of() :
                        p.getImages().stream().map(PostImage::getUrl).toList())
                .comments(comments)
                .build();
    }
}