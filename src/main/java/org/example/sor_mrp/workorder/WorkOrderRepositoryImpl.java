package org.example.sor_mrp.workorder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WorkOrderRepositoryImpl implements WorkOrderRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcPaginationHelper paginationHelper;

    private static final List<String> ALLOWED_COLUMNS = List.of(
            "id",
            "work_order_number",
            "status",
            "target_item_id",
            "quantity",
            "assigned_to",
            "created_by",
            "due_by",
            "created_at",
            "updated_at"
    );

    private static final List<String> SEARCHABLE_COLUMNS = List.of("work_order_number");

    public WorkOrderRepositoryImpl(
            NamedParameterJdbcTemplate jdbcTemplate,
            JdbcPaginationHelper paginationHelper) {
        this.jdbcTemplate = jdbcTemplate;
        this.paginationHelper = paginationHelper;
    }

    @Override
    public JdbcPaginationHelper.PaginatedResult<WorkOrder> findAllFiltered(
            Map<String, String> filters,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        return JdbcPaginationHelper.paginate(
                jdbcTemplate,
                "work_order",
                filters,
                search,
                SEARCHABLE_COLUMNS,
                ALLOWED_COLUMNS,
                page,
                size,
                sortBy,
                sortDir,
                this::mapWorkOrder);
    }

    private WorkOrder mapWorkOrder(ResultSet rs, int rowNum) throws SQLException {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(rs.getLong("id"));
        workOrder.setWorkOrderNumber(rs.getString("work_order_number"));

        String statusValue = rs.getString("status");
        if (statusValue != null) {
            workOrder.setStatus(WorkOrder.Status.valueOf(statusValue));
        }

        workOrder.setTargetItemId(rs.getLong("target_item_id"));
        workOrder.setQuantity(rs.getInt("quantity"));

        long assignedTo = rs.getLong("assigned_to");
        workOrder.setAssignedTo(rs.wasNull() ? null : assignedTo);

        workOrder.setCreatedBy(rs.getLong("created_by"));
        workOrder.setDueBy(toOffsetDateTime(rs.getTimestamp("due_by")));
        workOrder.setCreatedAt(toOffsetDateTime(rs.getTimestamp("created_at")));
        workOrder.setUpdatedAt(toOffsetDateTime(rs.getTimestamp("updated_at")));
        return workOrder;
    }

    private OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant().atOffset(ZoneOffset.UTC);
    }
}
