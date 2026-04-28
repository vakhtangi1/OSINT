package com.osint.backend.service;

import com.osint.backend.dto.AuthResponse;
import com.osint.backend.dto.LoginRequest;
import com.osint.backend.dto.RegisterRequest;
import com.osint.backend.model.UserAccount;
import com.osint.backend.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = clean(request.getUsername());
        String password = request.getPassword();
        String role = clean(request.getRole());

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username is required");
        }

        if (password == null || password.length() < 4) {
            throw new RuntimeException("Password must be at least 4 characters");
        }

        if (role == null || role.isBlank()) {
            role = "USER";
        }

        role = role.toUpperCase();

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            role = "USER";
        }

        if (userAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Username already exists");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        UserAccount saved = userAccountRepository.save(user);

        return new AuthResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getRole(),
                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {
        String username = clean(request.getUsername());
        String password = request.getPassword();

        UserAccount user = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                "Login successful"
        );
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}