package org.example.sor_mrp.workorder;

import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

public interface WorkOrderRepositoryCustom {
    JdbcPaginationHelper.PaginatedResult<WorkOrder> findAllFiltered(
            Map<String, String> filters,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDir);
}