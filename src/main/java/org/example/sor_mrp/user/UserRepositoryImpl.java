package org.example.sor_mrp.user;

import java.util.List;
import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final JdbcPaginationHelper paginationHelper;

  // Define allowed columns for filtering/sorting and searchable columns for global search
  private static final List<String> ALLOWED_COLUMNS =
      List.of(
          "id",
          "first_name",
          "last_name",
          "email",
          "auth_type",
          "role",
          "is_email_verified",
          "created_at",
          "updated_at");

  private static final List<String> SEARCHABLE_COLUMNS =
      List.of("first_name", "last_name", "email");

  public UserRepositoryImpl(
      NamedParameterJdbcTemplate jdbcTemplate, JdbcPaginationHelper paginationHelper) {
    this.jdbcTemplate = jdbcTemplate;
    this.paginationHelper = paginationHelper;
  }

  @Override
  public JdbcPaginationHelper.PaginatedResult<User> findAllFiltered(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir) {

    // Call the static paginate method on your helper
    return JdbcPaginationHelper.paginate(
        jdbcTemplate,
        "users", // Table name
        filters,
        search,
        SEARCHABLE_COLUMNS,
        ALLOWED_COLUMNS,
        page,
        size,
        sortBy,
        sortDir,
        (rs, rowNum) -> {
          User user = new User();
          user.setId(rs.getLong("id"));
          user.setFirstName(rs.getString("first_name"));
          user.setLastName(rs.getString("last_name"));
          user.setEmail(rs.getString("email"));
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
        });
  }
}
