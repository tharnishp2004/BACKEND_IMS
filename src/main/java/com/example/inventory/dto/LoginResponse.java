package com.example.inventory.dto;

public class LoginResponse {

    private String message;
    private Long id;
    private String name;
    private String username;
    private String role;

    public LoginResponse(
            String message,
            Long id,
            String name,
            String username,
            String role) {

        this.message = message;
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}