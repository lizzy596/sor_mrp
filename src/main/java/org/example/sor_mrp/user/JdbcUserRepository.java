package org.example.sor_mrp.user;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setAuthType(rs.getString("auth_type"));
        user.setRole(rs.getString("role"));
        user.setIsEmailVerified(rs.getBoolean("is_email_verified"));
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return user;
    };

    @Override
    public User save(User user) {
        String sql = """
        INSERT INTO users (first_name, last_name, email, password, auth_type, role, is_email_verified)
        VALUES (:firstName, :lastName, :email, :password, :authType, :role, :isEmailVerified)
        RETURNING id, created_at, updated_at
    """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("authType", user.getAuthType())
                .addValue("role", user.getRole() != null ? user.getRole() : "user")
                .addValue("isEmailVerified", user.getIsEmailVerified() != null ? user.getIsEmailVerified() : false);

        // Use queryForObject with a RowMapper to cleanly capture the returned ID and timestamps
        User savedUser = jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            user.setId(rs.getLong("id"));
            if (rs.getTimestamp("created_at") != null) {
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return user;
        });

        return savedUser;
    }
    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = :id";
        var params = new MapSqlParameterSource("id", id);
        var users = jdbcTemplate.query(sql, params, userRowMapper);
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = :email";
        var params = new MapSqlParameterSource("email", email);
        var users = jdbcTemplate.query(sql, params, userRowMapper);
        return users.stream().findFirst();
    }

    @Override
    public User update(Long id, User user) {
        String sql = """
            UPDATE users 
            SET first_name = :firstName, 
                last_name = :lastName, 
                role = :role, 
                updated_at = CURRENT_TIMESTAMP
            WHERE id = :id
            RETURNING updated_at
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("role", user.getRole());

        jdbcTemplate.query(sql, params, rs -> {
            if (rs.next() && rs.getTimestamp("updated_at") != null) {
                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
        });

        user.setId(id);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = :id";
        var params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }


    @Override
    public JdbcPaginationHelper.PaginatedResult<User> findAllFiltered(
            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
    ) {
        List<String> allowedColumns = List.of("id", "first_name", "last_name", "email", "role", "created_at", "updated_at");
        List<String> searchableColumns = List.of("first_name", "last_name", "email");

        return JdbcPaginationHelper.paginate(
                jdbcTemplate, // Your NamedParameterJdbcTemplate
                "users",      // Table name
                filters,
                search,
                searchableColumns,
                allowedColumns,
                page,
                size,
                sortBy,
                sortDir,
                userRowMapper
        );
    }
}


//package org.example.sor_mrp.repository;
//
//import org.example.sor_mrp.user.User;
//import org.example.sor_mrp.utils.JdbcPaginationHelper;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.stereotype.Repository;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Repository
//public class JdbcUserRepository implements UserRepository {
//
//    private final NamedParameterJdbcTemplate jdbcTemplate;
//
//    public JdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
//        User user = new User();
//        user.setId(rs.getLong("id"));
//        user.setFirstName(rs.getString("first_name"));
//        user.setLastName(rs.getString("last_name"));
//        user.setEmail(rs.getString("email"));
//        user.setPassword(rs.getString("password"));
//        user.setAuthType(rs.getString("auth_type"));
//        user.setRole(rs.getString("role"));
//        user.setIsEmailVerified(rs.getBoolean("is_email_verified"));
//        if (rs.getTimestamp("created_at") != null) {
//            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//        }
//        if (rs.getTimestamp("updated_at") != null) {
//            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//        }
//        return user;
//    };
//
//    @Override
//    public User save(User user) {
//        String sql = """
//        INSERT INTO users (first_name, last_name, email, password, auth_type, role, is_email_verified)
//        VALUES (:firstName, :lastName, :email, :password, :authType, :role, :isEmailVerified)
//        RETURNING id, created_at, updated_at
//    """;
//
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("firstName", user.getFirstName())
//                .addValue("lastName", user.getLastName())
//                .addValue("email", user.getEmail())
//                .addValue("password", user.getPassword())
//                .addValue("authType", user.getAuthType())
//                .addValue("role", user.getRole() != null ? user.getRole() : "user")
//                .addValue("isEmailVerified", user.getIsEmailVerified() != null ? user.getIsEmailVerified() : false);
//
//        // Use queryForObject with a RowMapper to cleanly capture the returned ID and timestamps
//        User savedUser = jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
//            user.setId(rs.getLong("id"));
//            if (rs.getTimestamp("created_at") != null) {
//                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
//            }
//            if (rs.getTimestamp("updated_at") != null) {
//                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//            }
//            return user;
//        });
//
//        return savedUser;
//    }
//    @Override
//    public Optional<User> findById(Long id) {
//        String sql = "SELECT * FROM users WHERE id = :id";
//        var params = new MapSqlParameterSource("id", id);
//        var users = jdbcTemplate.query(sql, params, userRowMapper);
//        return users.stream().findFirst();
//    }
//
//    @Override
//    public Optional<User> findByEmail(String email) {
//        String sql = "SELECT * FROM users WHERE email = :email";
//        var params = new MapSqlParameterSource("email", email);
//        var users = jdbcTemplate.query(sql, params, userRowMapper);
//        return users.stream().findFirst();
//    }
//
//    @Override
//    public User update(Long id, User user) {
//        String sql = """
//            UPDATE users
//            SET first_name = :firstName,
//                last_name = :lastName,
//                role = :role,
//                updated_at = CURRENT_TIMESTAMP
//            WHERE id = :id
//            RETURNING updated_at
//        """;
//
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("id", id)
//                .addValue("firstName", user.getFirstName())
//                .addValue("lastName", user.getLastName())
//                .addValue("role", user.getRole());
//
//        jdbcTemplate.query(sql, params, rs -> {
//            if (rs.next() && rs.getTimestamp("updated_at") != null) {
//                user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
//            }
//        });
//
//        user.setId(id);
//        return user;
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        String sql = "DELETE FROM users WHERE id = :id";
//        var params = new MapSqlParameterSource("id", id);
//        jdbcTemplate.update(sql, params);
//    }
//
//
//    @Override
//    public JdbcPaginationHelper.PaginatedResult<User> findAllFiltered(
//            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
//    ) {
//        List<String> allowedColumns = List.of("id", "first_name", "last_name", "email", "role", "created_at", "updated_at");
//        List<String> searchableColumns = List.of("first_name", "last_name", "email");
//
//        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
//        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1");
//        MapSqlParameterSource params = new MapSqlParameterSource();
//
//        // 1. Dynamic property filters (e.g., role = 'user')
//        if (filters != null) {
//            int i = 0;
//            for (Map.Entry<String, String> entry : filters.entrySet()) {
//                String field = entry.getKey();
//                String value = entry.getValue();
//                if (value != null && !value.isBlank() && allowedColumns.contains(field)) {
//                    String paramName = "filter_" + i;
//                    sql.append(" AND ").append(field).append(" = :").append(paramName);
//                    countSql.append(" AND ").append(field).append(" = :").append(paramName);
//                    params.addValue(paramName, value);
//                    i++;
//                }
//            }
//        }
//
//        // 2. Global search across searchable columns
//        if (search != null && !search.isBlank() && searchableColumns != null && !searchableColumns.isEmpty()) {
//            sql.append(" AND (");
//            countSql.append(" AND (");
//            List<String> searchClauses = new ArrayList<>();
//            String searchPattern = "%" + search.toLowerCase() + "%";
//
//            int j = 0;
//            for (String col : searchableColumns) {
//                if (allowedColumns.contains(col)) {
//                    String paramName = "search_" + j;
//                    searchClauses.add("LOWER(" + col + ") LIKE :" + paramName);
//                    params.addValue(paramName, searchPattern);
//                    j++;
//                }
//            }
//
//            sql.append(String.join(" OR ", searchClauses.toArray(new String[0])));
//            countSql.append(String.join(" OR ", searchClauses.toArray(new String[0])));
//            sql.append(")");
//            countSql.append(")");
//        }
//
//        // 3. Get total count
//        Long totalResults = jdbcTemplate.queryForObject(countSql.toString(), params, Long.class);
//        if (totalResults == null) totalResults = 0L;
//
//        // 4. Sorting & Pagination (LIMIT / OFFSET)
//        if (sortBy != null && allowedColumns.contains(sortBy)) {
//            String direction = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
//            sql.append(" ORDER BY ").append(sortBy).append(" ").append(direction);
//        } else {
//            sql.append(" ORDER BY created_at DESC");
//        }
//
//        sql.append(" LIMIT :limit OFFSET :offset");
//        params.addValue("limit", size);
//        params.addValue("offset", page * size);
//
//        // 5. Execute query
//        List<User> results = jdbcTemplate.query(sql.toString(), params, (RowMapper<User>) userRowMapper);
//
//        int totalPages = (int) Math.ceil((double) totalResults / size);
//
//        return new JdbcPaginationHelper.PaginatedResult<>(results, totalResults, totalPages, page + 1, size);
//    }
//}