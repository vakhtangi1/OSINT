package com.osint.backend.service;

import com.osint.backend.dto.AuthResponse;
import com.osint.backend.dto.LoginRequest;
import com.osint.backend.dto.MfaVerifyRequest;
import com.osint.backend.dto.RegisterRequest;
import com.osint.backend.model.AuditLog;
import com.osint.backend.model.UserAccount;
import com.osint.backend.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    private final Map<String, MfaSession> mfaSessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public AuthService(UserAccountRepository userAccountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuditLogService auditLogService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.auditLogService = auditLogService;
    }

    public AuthResponse register(RegisterRequest request) {
        String username = clean(request.getUsername());
        String password = request.getPassword();
        String role = clean(request.getRole());

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username is required");
        }

        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        role = (role == null || role.isBlank()) ? "USER" : role.toUpperCase();

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

        auditLogService.log(username, AuditLog.Action.REGISTER, "New account registered");

        return new AuthResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getRole(),
                "Registration successful. Please login.",
                null
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

        String code = generateMfaCode();

        mfaSessions.put(
                user.getUsername().toLowerCase(),
                new MfaSession(code, LocalDateTime.now().plusMinutes(5))
        );

        auditLogService.log(user.getUsername(), AuditLog.Action.LOGIN, "Password accepted, MFA required");

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                "MFA code required",
                null,
                true,
                code
        );
    }

    public AuthResponse verifyMfa(MfaVerifyRequest request) {
        String username = clean(request.getUsername());
        String code = clean(request.getCode());

        if (username == null || code == null) {
            throw new RuntimeException("Username and MFA code are required");
        }

        UserAccount user = userAccountRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Invalid MFA session"));

        MfaSession session = mfaSessions.get(username.toLowerCase());

        if (session == null) {
            throw new RuntimeException("MFA session expired or not found");
        }

        if (LocalDateTime.now().isAfter(session.expiresAt())) {
            mfaSessions.remove(username.toLowerCase());
            throw new RuntimeException("MFA code expired");
        }

        if (!session.code().equals(code)) {
            throw new RuntimeException("Invalid MFA code");
        }

        mfaSessions.remove(username.toLowerCase());

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        auditLogService.log(user.getUsername(), AuditLog.Action.LOGIN, "MFA verified, login successful");

        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                "Login successful",
                token
        );
    }

    private String generateMfaCode() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private record MfaSession(String code, LocalDateTime expiresAt) {}
}