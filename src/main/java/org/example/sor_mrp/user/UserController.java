package org.example.sor_mrp.user;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.example.sor_mrp.shared.configuration.security.AuthDtos;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'manageUsers')")
  public ResponseEntity<User> createUser(@Valid @RequestBody UserRegistrationRequestDto request) {
    User createdUser = userService.createUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }

  // GET /api/users?role=user&search=john&page=1&limit=10&sortBy=created_at&sortDir=DESC
  @GetMapping
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'manageUsers')")
  public ResponseEntity<?> getUsers(
      @RequestParam Map<String, String> allParams,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "created_at") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDir,
      @RequestParam(required = false) String search) {
    // Separate out pagination/sorting parameters from custom table property filters
    Map<String, String> filters = new HashMap<>(allParams);
    filters.remove("page");
    filters.remove("limit");
    filters.remove("sortBy");
    filters.remove("sortDir");
    filters.remove("search");

    JdbcPaginationHelper.PaginatedResult<User> result =
        userService.queryUsers(filters, search, page, limit, sortBy, sortDir);

    // Map entities to safe response DTOs to hide private fields (like passwords)
    var userResponses = result.results().stream().map(AuthDtos.UserResponse::fromUser).toList();

    return ResponseEntity.ok(
        Map.of(
            "results", userResponses,
            "page", result.page(),
            "limit", result.limit(),
            "totalPages", result.totalPages(),
            "totalResults", result.totalResults()));
  }

  @GetMapping("/{id}")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, #id.toString(), 'getUsers')")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    return userService
        .getUserById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/email")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'getUsers')")
  public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
    return userService
        .getUserByEmail(email)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, #id.toString(), 'manageUsers')")
  public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody User user) {
    User updatedUser = userService.updateUserById(id, user);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'deleteUser')")
  public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
    userService.deleteUserById(id);
    return ResponseEntity.noContent().build();
  }
}
