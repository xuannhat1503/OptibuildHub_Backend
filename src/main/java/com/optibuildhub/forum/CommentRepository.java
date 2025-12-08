package com.optibuildhub.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPostId(Long postId);

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
    
    void deleteByPostId(Long postId);
    
    void deleteByParentId(Long parentId);
}