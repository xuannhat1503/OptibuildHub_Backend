package com.optibuildhub.admin;

import com.optibuildhub.common.ApiResponse;
import com.optibuildhub.forum.Comment;
import com.optibuildhub.forum.CommentRepository;
import com.optibuildhub.forum.PostRepository;
import com.optibuildhub.user.User;
import com.optibuildhub.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final CommentRepository commentRepo;
    private final PostRepository postRepo;

    // User Management
    @GetMapping("/users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        var users = userRepo.findAll();
        var responses = users.stream()
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getRole().name()))
                .toList();
        return ApiResponse.ok(responses);
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
        return ApiResponse.ok("User deleted");
    }

    @PutMapping("/users/{id}/role")
    public ApiResponse<String> updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequest req) {
        var user = userRepo.findById(id).orElseThrow();
        user.setRole(com.optibuildhub.user.Role.valueOf(req.role()));
        userRepo.save(user);
        return ApiResponse.ok("Role updated");
    }

    // Comment Management
    @GetMapping("/comments")
    public ApiResponse<List<CommentResponse>> getAllComments() {
        var comments = commentRepo.findAll();
        var responses = comments.stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getContent(),
                        c.getUser() != null ? c.getUser().getFullName() : "Unknown",
                        c.getPost() != null ? c.getPost().getTitle() : "Unknown",
                        c.getCreatedAt()
                ))
                .toList();
        return ApiResponse.ok(responses);
    }

    @DeleteMapping("/comments/{id}")
    public ApiResponse<String> deleteComment(@PathVariable Long id) {
        commentRepo.deleteById(id);
        return ApiResponse.ok("Comment deleted");
    }

    // DTOs
    record UserResponse(Long id, String email, String fullName, String role) {}
    record RoleUpdateRequest(String role) {}
    record CommentResponse(Long id, String content, String userName, String postTitle, java.time.Instant createdAt) {}
}
