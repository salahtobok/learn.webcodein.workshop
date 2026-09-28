package com.webcodein.workshop.refactored.adapter.in;

import com.webcodein.workshop.refactored.application.UserService;
import com.webcodein.workshop.refactored.domain.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User register(@RequestParam String username, @RequestParam String email) {
        return userService.registerUser(username, email);
    }
}
