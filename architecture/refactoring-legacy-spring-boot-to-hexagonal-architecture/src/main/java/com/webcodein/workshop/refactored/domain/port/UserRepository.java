package com.webcodein.workshop.refactored.domain.port;

import com.webcodein.workshop.refactored.domain.User;

public interface UserRepository {
    User save(User user);
}
