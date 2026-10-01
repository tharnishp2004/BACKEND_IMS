package com.example.inventory.controller;

import com.example.inventory.entity.User;
import com.example.inventory.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRole() != null) {
            user.setRole(user.getRole().toUpperCase());
        }
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getName() != null) existingUser.setName(user.getName());
        if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            if (!user.getPassword().startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                existingUser.setPassword(user.getPassword());
            }
        }
        if (user.getRole() != null) existingUser.setRole(user.getRole().toUpperCase());

        return userRepository.save(existingUser);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);

        // Reset AUTO_INCREMENT so the next new user gets the next sequential ID
        try {
            Long maxId = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(MAX(id), 0) FROM users", Long.class);
            jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = " + (maxId + 1));
        } catch (Exception ignored) {}

        return "User deleted successfully";
    }
}