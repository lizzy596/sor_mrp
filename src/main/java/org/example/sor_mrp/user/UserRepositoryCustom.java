package org.example.sor_mrp.user;

import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

public interface UserRepositoryCustom {
  JdbcPaginationHelper.PaginatedResult<User> findAllFiltered(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir);
}
