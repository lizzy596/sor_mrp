package org.example.sor_mrp.workorder;

import java.time.OffsetDateTime;

public record WorkOrderResponseDto(
        Long id,
        String workOrderNumber,
        WorkOrder.Status status,
        Long targetItemId,
        Integer quantity,
        Long assignedTo,
        Long createdBy,
        OffsetDateTime dueBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static WorkOrderResponseDto from(WorkOrder entity) {
        if (entity == null) {
            return null;
        }
        return new WorkOrderResponseDto(
                entity.getId(),
                entity.getWorkOrderNumber(),
                entity.getStatus(),
                entity.getTargetItemId(),
                entity.getQuantity(),
                entity.getAssignedTo(),
                entity.getCreatedBy(),
                entity.getDueBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static WorkOrderResponseDto fromWorkOrder(WorkOrder entity) {
        return from(entity);
    }
}
