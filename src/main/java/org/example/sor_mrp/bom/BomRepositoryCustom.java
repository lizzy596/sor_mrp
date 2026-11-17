package org.example.sor_mrp.bom;

import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

public interface BomRepositoryCustom {
  JdbcPaginationHelper.PaginatedResult<Bom> findAllFiltered(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir);
}
