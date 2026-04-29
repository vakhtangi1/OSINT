package com.osint.backend.dto;

public class MfaVerifyRequest {

    private String username;
    private String code;

    public MfaVerifyRequest() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}