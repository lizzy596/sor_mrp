package org.example.sor_mrp.bom;


import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

import java.util.Map;

public interface BomRepositoryCustom {
    JdbcPaginationHelper.PaginatedResult<Bom> findAllFiltered(
            Map<String, String> filters,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDir);
}