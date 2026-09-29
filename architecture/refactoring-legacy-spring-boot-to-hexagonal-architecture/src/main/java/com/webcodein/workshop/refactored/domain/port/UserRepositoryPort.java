package com.webcodein.workshop.refactored.domain.port;

import com.webcodein.workshop.refactored.domain.User;

public interface UserRepositoryPort {
    User save(User user);
}
