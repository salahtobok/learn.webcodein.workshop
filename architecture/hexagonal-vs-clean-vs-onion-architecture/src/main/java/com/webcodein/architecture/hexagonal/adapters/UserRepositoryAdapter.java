package com.webcodein.architecture.hexagonal.adapters;
import com.webcodein.architecture.hexagonal.domain.User;
import com.webcodein.architecture.hexagonal.ports.UserRepositoryPort;
import org.springframework.stereotype.Repository;
@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {
    @Override
    public User save(User user) {
        return user; // mocked save
    }
}
