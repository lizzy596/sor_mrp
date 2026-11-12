package org.example.sor_mrp.auth;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import org.example.sor_mrp.shared.configuration.security.AuthDtos;
import org.example.sor_mrp.shared.configuration.security.CookieService;
import org.example.sor_mrp.user.User;
import org.example.sor_mrp.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final CookieService cookieService;

  public AuthenticationController(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      TokenRepository tokenRepository,
      TokenService tokenService,
      PasswordEncoder passwordEncoder,
      CookieService cookieService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.tokenService = tokenService;
    this.cookieService = cookieService;
    this.passwordEncoder = passwordEncoder;
  }

  // POST /api/auth/register
  @PostMapping("/register")
  public ResponseEntity<?> register(
      @RequestBody AuthDtos.RegisterRequest request, HttpServletResponse response) {
    try {
      if (userRepository.findByEmail(request.email()).isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("message", "Email already taken"));
      }

      User newUser = new User();
      newUser.setFirstName(request.firstName());
      newUser.setLastName(request.lastName());
      newUser.setEmail(request.email());
      newUser.setPassword(passwordEncoder.encode(request.password()));
      newUser.setAuthType("email");
      newUser.setRole(request.role() != null ? request.role() : "user");
      newUser.setIsEmailVerified(false);

      User savedUser = userRepository.save(newUser);
      TokenService.AuthTokensResponse tokens = tokenService.generateAuthTokens(savedUser);
      cookieService.setTokenCookie(response, tokens.refreshToken(), tokens.refreshExpires());

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Map.of("user", AuthDtos.UserResponse.fromUser(savedUser), "tokens", tokens));
    } catch (Exception e) {
      e.printStackTrace(); // This will print the exact crash reason in your IntelliJ/terminal
      // console
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(
              Map.of("message", e.getMessage() != null ? e.getMessage() : "Unknown server error"));
    }
  }

  // POST /api/auth/login/email
  @PostMapping("/login/email")
  public ResponseEntity<?> login(
      @RequestBody AuthDtos.LoginRequest request, HttpServletResponse response) {
    try {
      User user =
          userRepository
              .findByEmail(request.email())
              .orElseThrow(() -> new RuntimeException("Incorrect email or password"));

      if (!"email".equals(user.getAuthType())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "Incorrect email or password"));
      }

      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));

      TokenService.AuthTokensResponse tokens = tokenService.generateAuthTokens(user);
      cookieService.setTokenCookie(response, tokens.refreshToken(), tokens.refreshExpires());

      return ResponseEntity.ok(
          Map.of("user", AuthDtos.UserResponse.fromUser(user), "tokens", tokens));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Incorrect email or password"));
    }
  }

  // POST /api/auth/logout
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestBody(required = false) AuthDtos.RefreshTokenRequest request,
      @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
      HttpServletResponse response) {
    // Fallback to checking request body or cookie for the refresh token
    String tokenToRevoke =
        (request != null && request.refreshToken() != null)
            ? request.refreshToken()
            : cookieRefreshToken;

    if (tokenToRevoke != null) {
      Optional<Token> tokenDoc =
          tokenRepository.findByTokenAndType(tokenToRevoke, Token.TokenType.REFRESH);
      tokenDoc.ifPresent(record -> tokenRepository.deleteById(record.getId()));
    }

    // Expire/clear the refresh token cookie on logout
    cookieService.expireTokenCookie(response);

    return ResponseEntity.noContent().build();
  }

  // POST /api/auth/refresh-tokens
  @PostMapping("/refresh-tokens")
  public ResponseEntity<?> refreshTokens(@RequestBody AuthDtos.RefreshTokenRequest request) {
    try {
      Token tokenDoc = tokenService.verifyToken(request.refreshToken(), Token.TokenType.REFRESH);
      User user =
          userRepository
              .findById(tokenDoc.getUserId())
              .orElseThrow(() -> new RuntimeException("User not found"));

      // Delete old refresh token (rotation)
      tokenRepository.deleteById(tokenDoc.getId());

      // Generate fresh token pair
      TokenService.AuthTokensResponse tokens = tokenService.generateAuthTokens(user);

      return ResponseEntity.ok(
          Map.of("user", AuthDtos.UserResponse.fromUser(user), "tokens", tokens));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Please authenticate"));
    }
  }

  // POST /api/auth/forgot-password
  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(@RequestBody AuthDtos.ForgotPasswordRequest request) {
    String resetToken = tokenService.generateResetPasswordToken(request.email());

    // TODO: Hook up your EmailService here to send resetToken to email
    System.out.println("Password Reset Token for " + request.email() + ": " + resetToken);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // POST /api/auth/reset-password?token=...
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(
      @RequestParam("token") String token, @RequestBody AuthDtos.ResetPasswordRequest request) {
    try {
      Token tokenDoc = tokenService.verifyToken(token, Token.TokenType.RESET_PASSWORD);

      User user =
          userRepository
              .findById(tokenDoc.getUserId())
              .orElseThrow(() -> new RuntimeException("User not found"));

      user.setPassword(passwordEncoder.encode(request.password()));
      userRepository.update(user.getId(), user);

      // Clean up reset tokens for user
      tokenRepository.deleteById(tokenDoc.getId());

      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Password reset failed"));
    }
  }

  // POST /api/auth/send-verification-email
  @PostMapping("/send-verification-email")
  public ResponseEntity<Void> sendVerificationEmail(@RequestParam("email") String email) {
    User user =
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    String verifyToken = tokenService.generateVerifyEmailToken(user);

    // TODO: Hook up your EmailService here to send verification link
    System.out.println("Email Verification Token for " + email + ": " + verifyToken);

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  // POST /api/auth/verify-email?token=...
  @PostMapping("/verify-email")
  public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
    try {
      Token tokenDoc = tokenService.verifyToken(token, Token.TokenType.VERIFY_EMAIL);
      User user =
          userRepository
              .findById(tokenDoc.getUserId())
              .orElseThrow(() -> new RuntimeException("User not found"));

      user.setIsEmailVerified(true);
      userRepository.update(user.getId(), user);

      tokenRepository.deleteById(tokenDoc.getId());

      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Email verification failed"));
    }
  }
}
