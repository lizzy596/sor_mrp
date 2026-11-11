package org.example.sor_mrp.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserRegistrationRequestDto request) {
        // 1. Check if passwords match
        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // 2. Check if email is already taken
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already taken.");
        }

        // 3. Map DTO to Domain Model and encode password
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encodes and salts with BCrypt
        user.setAuthType(request.getAuthType());
        user.setRole(request.getRole() != null ? request.getRole() : "user");
        user.setIsEmailVerified(false);

        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUserById(Long id, User updatedDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        existingUser.setFirstName(updatedDetails.getFirstName());
        existingUser.setLastName(updatedDetails.getLastName());
        existingUser.setRole(updatedDetails.getRole());

        return userRepository.update(id, existingUser);
    }

    public void deleteUserById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public JdbcPaginationHelper.PaginatedResult<User> queryUsers(
            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
    ) {
        // Adjust for 0-indexed pages if client sends 1-indexed (e.g., page=1 becomes 0)
        int pageIndex = Math.max(0, page - 1);
        return userRepository.findAllFiltered(filters, search, pageIndex, size, sortBy, sortDir);
    }
}