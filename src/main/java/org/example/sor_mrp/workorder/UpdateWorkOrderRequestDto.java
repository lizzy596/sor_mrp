package org.example.sor_mrp.workorder;

import jakarta.validation.constraints.Positive;
import java.time.OffsetDateTime;

public record UpdateWorkOrderRequestDto(
        @Positive(message = "assignedTo must be a positive ID")
        Long assignedTo,

        WorkOrder.Status status,

        @Positive(message = "quantity must be greater than 0")
        Integer quantity,

        OffsetDateTime dueBy
) {
}
