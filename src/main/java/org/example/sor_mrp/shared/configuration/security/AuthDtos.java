package org.example.sor_mrp.shared.configuration.security;

import org.example.sor_mrp.user.User;

public class AuthDtos {
  public record RegisterRequest(
      String firstName, String lastName, String email, String password, String role) {}

  public record RefreshTokenRequest(String refreshToken) {}

  public record ForgotPasswordRequest(String email) {}

  public record ResetPasswordRequest(String password) {}

  public record LoginRequest(String email, String password) {}

  public record UserResponse(
      Long id,
      String firstName,
      String lastName,
      String email,
      String role,
      String authType,
      Object authorities,
      Boolean isEmailVerified,
      String createdAt,
      String updatedAt) {
    public static UserResponse fromUser(User user) {
      return new UserResponse(
          user.getId(),
          user.getFirstName(),
          user.getLastName(),
          user.getEmail(),
          user.getRole(),
          user.getAuthType(),
          user.getAuthorities(),
          user.getIsEmailVerified(),
          user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
          user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
    }
  }
}
