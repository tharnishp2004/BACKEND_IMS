package com.example.inventory.dto;

public class LoginResponse {

    private String message;
    private String token;
    private Long id;
    private String name;
    private String username;
    private String role;

    public LoginResponse() {
    }

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

    public LoginResponse(
            String message,
            String token,
            Long id,
            String name,
            String username,
            String role) {

        this.message = message;
        this.token = token;
        this.id = id;
        this.name = name;
        this.username = username;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}