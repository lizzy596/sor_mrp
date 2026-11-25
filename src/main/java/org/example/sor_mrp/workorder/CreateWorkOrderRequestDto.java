package org.example.sor_mrp.workorder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record CreateWorkOrderRequestDto(
        @NotBlank(message = "workOrderNumber is required")
        @Size(max = 64, message = "workOrderNumber must be at most 64 characters")
        String workOrderNumber,

        @NotNull(message = "targetItemId is required")
        @Positive(message = "targetItemId must be a positive ID")
        Long targetItemId,

        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than 0")
        Integer quantity,

        @Positive(message = "assignedTo must be a positive ID")
        Long assignedTo,

        WorkOrder.Status status,

        OffsetDateTime dueBy
) {
}
