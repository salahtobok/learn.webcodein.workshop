package com.webcodein.workshop.refactored.application;

import com.webcodein.workshop.refactored.domain.User;

public interface RegisterUserUseCase {
    User register(String username, String email);
}
