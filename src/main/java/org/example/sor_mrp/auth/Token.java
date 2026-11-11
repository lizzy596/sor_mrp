package org.example.sor_mrp.auth;

import java.time.LocalDateTime;

public class Token {

    public enum TokenType {
        REFRESH, ACCESS, RESET_PASSWORD, VERIFY_EMAIL
    }

    private Long id;
    private Long userId;
    private String token;
    private TokenType tokenType;
    private LocalDateTime expiresAt;
    private Boolean blacklisted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Token() {}

    public Token(Long userId, String token, TokenType tokenType, LocalDateTime expiresAt) {
        this.userId = userId;
        this.token = token;
        this.tokenType = tokenType;
        this.expiresAt = expiresAt;
        this.blacklisted = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(Boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TokenRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", tokenType=" + tokenType +
                ", expiresAt=" + expiresAt +
                ", blacklisted=" + blacklisted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}