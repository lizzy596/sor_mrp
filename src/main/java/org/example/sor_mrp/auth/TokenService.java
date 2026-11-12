package org.example.sor_mrp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.example.sor_mrp.user.User;
import org.example.sor_mrp.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final SecretKey jwtSecretKey;

  @Value("${jwt.access-expiration-minutes:15}")
  private long accessExpirationMinutes;

  @Value("${jwt.refresh-expiration-days:30}")
  private long refreshExpirationDays;

  @Value("${jwt.reset-password-expiration-minutes:10}")
  private long resetPasswordExpirationMinutes;

  @Value("${jwt.verify-email-expiration-minutes:1440}")
  private long verifyEmailExpirationMinutes;

  public TokenService(
      TokenRepository tokenRepository,
      UserRepository userRepository,
      @Value("${jwt.secret:defaultSecretKeyThatShouldBeAtLeast32BytesLong}") String secret) {
    this.tokenRepository = tokenRepository;
    this.userRepository = userRepository;
    this.jwtSecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /** Generate raw JWT token string */
  public String generateToken(Long userId, LocalDateTime expiresAt, String type) {
    Instant expiryInstant = expiresAt.atZone(ZoneId.systemDefault()).toInstant();

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(new Date())
        .expiration(Date.from(expiryInstant))
        .claim("type", type)
        .signWith(jwtSecretKey)
        .compact();
  }

  /** Hash token using SHA-256 before database persistence */
  private String hashToken(String rawToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(encodedHash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Could not hash token", e);
    }
  }

  /** Save hashed token to PostgreSQL */
  public Token saveToken(
      String rawToken,
      Long userId,
      LocalDateTime expiresAt,
      Token.TokenType type,
      boolean blacklisted) {
    String hashedToken = hashToken(rawToken);
    Token tokenRecord = new Token(userId, hashedToken, type, expiresAt);
    tokenRecord.setBlacklisted(blacklisted);
    return tokenRepository.save(tokenRecord);
  }

  /** Generate access and refresh tokens upon login */
  public AuthTokensResponse generateAuthTokens(User user) {
    LocalDateTime accessExpires = LocalDateTime.now().plusMinutes(accessExpirationMinutes);
    String accessToken = generateToken(user.getId(), accessExpires, Token.TokenType.ACCESS.name());

    LocalDateTime refreshExpires = LocalDateTime.now().plusDays(refreshExpirationDays);
    String refreshToken =
        generateToken(user.getId(), refreshExpires, Token.TokenType.REFRESH.name());

    // Save the HASHED version in the database, return the raw token to the caller
    saveToken(refreshToken, user.getId(), refreshExpires, Token.TokenType.REFRESH, false);

    return new AuthTokensResponse(accessToken, accessExpires, refreshToken, refreshExpires);
  }

  /** Verify token signature, expiration, and extract userId from subject */
  public Long extractUserId(String token, Token.TokenType type) {
    Claims claims =
        Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();

    String tokenTypeClaim = claims.get("type", String.class);
    if (!type.name().equals(tokenTypeClaim)) {
      throw new IllegalArgumentException("Invalid token type");
    }

    return Long.valueOf(claims.getSubject());
  }

  /** Verify token signature, expiration, and database record status using the hash */
  public Token verifyToken(String rawToken, Token.TokenType type) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(rawToken).getPayload();

      Long userId = Long.valueOf(claims.getSubject());
      String tokenTypeClaim = claims.get("type", String.class);

      if (!type.name().equals(tokenTypeClaim)) {
        throw new IllegalArgumentException("Invalid token type");
      }

      // Hash the incoming raw token to look up the database record
      String hashedToken = hashToken(rawToken);

      Optional<Token> tokenDoc =
          tokenRepository.findByTokenAndTypeAndUserIdAndBlacklistedFalse(hashedToken, type, userId);

      if (tokenDoc.isEmpty()) {
        throw new RuntimeException("Token not found or has been revoked");
      }

      return tokenDoc.get();
    } catch (JwtException | IllegalArgumentException e) {
      throw new RuntimeException("Token verification failed: " + e.getMessage());
    }
  }

  /** Generate password reset token */
  public String generateResetPasswordToken(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("No users found with this email"));

    LocalDateTime expires = LocalDateTime.now().plusMinutes(resetPasswordExpirationMinutes);
    String token = generateToken(user.getId(), expires, Token.TokenType.RESET_PASSWORD.name());

    saveToken(token, user.getId(), expires, Token.TokenType.RESET_PASSWORD, false);
    return token;
  }

  /** Generate email verification token */
  public String generateVerifyEmailToken(User user) {
    LocalDateTime expires = LocalDateTime.now().plusMinutes(verifyEmailExpirationMinutes);
    String token = generateToken(user.getId(), expires, Token.TokenType.VERIFY_EMAIL.name());

    saveToken(token, user.getId(), expires, Token.TokenType.VERIFY_EMAIL, false);
    return token;
  }

  /** Clear all user tokens (Logout / Clear session) */
  public void clearUserTokens(Long userId) {
    tokenRepository.deleteByUserId(userId);
  }

  // Helper Response DTO record
  public record AuthTokensResponse(
      String accessToken,
      LocalDateTime accessExpires,
      String refreshToken,
      LocalDateTime refreshExpires) {}
}
