package com.osint.backend.dto;

public class AuthResponse {

    private Long userId;
    private String username;
    private String role;
    private String message;
    private String token;
    private boolean mfaRequired;
    private String devMfaCode;

    public AuthResponse() {}

    public AuthResponse(Long userId, String username, String role, String message, String token) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.message = message;
        this.token = token;
        this.mfaRequired = false;
    }

    public AuthResponse(Long userId, String username, String role, String message, String token,
                        boolean mfaRequired, String devMfaCode) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.message = message;
        this.token = token;
        this.mfaRequired = mfaRequired;
        this.devMfaCode = devMfaCode;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public boolean isMfaRequired() { return mfaRequired; }
    public String getDevMfaCode() { return devMfaCode; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
    public void setMessage(String message) { this.message = message; }
    public void setToken(String token) { this.token = token; }
    public void setMfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; }
    public void setDevMfaCode(String devMfaCode) { this.devMfaCode = devMfaCode; }
}