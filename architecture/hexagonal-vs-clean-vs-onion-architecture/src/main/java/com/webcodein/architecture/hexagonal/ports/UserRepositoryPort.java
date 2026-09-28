package com.webcodein.architecture.hexagonal.ports;
import com.webcodein.architecture.hexagonal.domain.User;
public interface UserRepositoryPort {
    User save(User user);
}
