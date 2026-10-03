package com.webcodein.legacy.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    // Direct field injection (bad practice often found in legacy)
    // Here we just have a simple controller.
    
    @GetMapping("/users")
    public String getUsers() {
        return "Users List";
    }
}
