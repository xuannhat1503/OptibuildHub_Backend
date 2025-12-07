package com.optibuildhub.user;

import com.optibuildhub.common.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepo;

    public User mustFind(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }
}