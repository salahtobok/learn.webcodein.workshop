package com.webcodein.workshop.refactored.application;

import com.webcodein.workshop.refactored.domain.User;
import com.webcodein.workshop.refactored.domain.port.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email) {
        User user = new User(null, username, email);
        return userRepository.save(user);
    }
}
