package com.webcodein.workshop.refactored.application;

import com.webcodein.workshop.refactored.domain.User;
import com.webcodein.workshop.refactored.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepositoryPort userRepositoryPort;

    public RegisterUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User register(String username, String email) {
        User user = new User(null, username, email);
        return userRepositoryPort.save(user);
    }
}
