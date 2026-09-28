package com.webcodein.workshop.refactored.domain;

public class User {
    private final Long id;
    private final String username;
    private final String email;

    public User(Long id, String username, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}
