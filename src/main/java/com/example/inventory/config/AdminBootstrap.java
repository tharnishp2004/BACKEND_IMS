package com.example.inventory.config;

import com.example.inventory.entity.User;
import com.example.inventory.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Environment environment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String username = environment.getProperty("ADMIN_USERNAME");
        String password = environment.getProperty("ADMIN_PASSWORD");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            log.info("Admin bootstrap skipped; ADMIN_USERNAME and ADMIN_PASSWORD are not both configured.");
            return;
        }

        if (password.length() < 3 || password.length() > 10) {
            throw new IllegalStateException("ADMIN_PASSWORD must be between 3 and 10 characters.");
        }

        User admin = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName("Administrator");
                    newUser.setUsername(username);
                    return newUser;
                });

        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole("ADMIN");
        userRepository.save(admin);

        log.info("Administrator account '{}' is ready.", username);
    }
}
