package org.example.sor_mrp.item;

import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

public interface ItemRepositoryCustom {
  JdbcPaginationHelper.PaginatedResult<Item> findAllFiltered(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir);
}
