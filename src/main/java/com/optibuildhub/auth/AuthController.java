package com.optibuildhub.auth;

import com.optibuildhub.auth.dto.*;
import com.optibuildhub.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.ok(authService.getCurrentUser(userDetails.getUsername()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        // Stateless JWT - just return success
        // Client will remove token from localStorage
        return ApiResponse.ok("Logged out successfully");
    }
}
