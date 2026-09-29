package com.webcodein.workshop.refactored.adapter.in;

import com.webcodein.workshop.refactored.application.RegisterUserUseCase;
import com.webcodein.workshop.refactored.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserWebAdapter {
    private final RegisterUserUseCase registerUserUseCase;

    public UserWebAdapter(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@RequestParam String username, @RequestParam String email) {
        User user = registerUserUseCase.register(username, email);
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername()));
    }
}
