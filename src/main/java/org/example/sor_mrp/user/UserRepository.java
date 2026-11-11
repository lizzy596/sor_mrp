package org.example.sor_mrp.user;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User update(Long id, User user);
    void deleteById(Long id);

    JdbcPaginationHelper.PaginatedResult<User> findAllFiltered(
            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
    );
}