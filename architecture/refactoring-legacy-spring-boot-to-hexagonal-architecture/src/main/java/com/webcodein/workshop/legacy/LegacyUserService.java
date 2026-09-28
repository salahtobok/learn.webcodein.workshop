package com.webcodein.workshop.legacy;

import org.springframework.stereotype.Service;

@Service
public class LegacyUserService {
    private final LegacyUserRepository userRepository;

    public LegacyUserService(LegacyUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }
}
