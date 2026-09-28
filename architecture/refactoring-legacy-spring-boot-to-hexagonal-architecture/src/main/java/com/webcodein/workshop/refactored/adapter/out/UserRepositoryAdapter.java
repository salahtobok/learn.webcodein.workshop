package com.webcodein.workshop.refactored.adapter.out;

import com.webcodein.workshop.refactored.domain.User;
import com.webcodein.workshop.refactored.domain.port.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepository {
    private final SpringDataUserRepository repository;

    public UserRepositoryAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        UserJpaEntity saved = repository.save(entity);
        return new User(saved.getId(), saved.getUsername(), saved.getEmail());
    }
}
