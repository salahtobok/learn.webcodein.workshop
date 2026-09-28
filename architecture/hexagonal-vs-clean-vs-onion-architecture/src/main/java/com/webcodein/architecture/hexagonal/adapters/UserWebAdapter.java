package com.webcodein.architecture.hexagonal.adapters;
import com.webcodein.architecture.hexagonal.domain.User;
import com.webcodein.architecture.hexagonal.ports.UserRepositoryPort;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/hex/users")
public class UserWebAdapter {
    private final UserRepositoryPort userRepositoryPort;
    public UserWebAdapter(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepositoryPort.save(user);
    }
}
