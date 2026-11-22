package org.example.sor_mrp.workorder;

import jakarta.validation.constraints.Positive;

public record UpdateWorkOrderRequestDto(
        Long assignedTo,

        WorkOrder.Status status,

        @Positive(message = "quantity must be greater than 0")
        Integer quantity
) {
}
