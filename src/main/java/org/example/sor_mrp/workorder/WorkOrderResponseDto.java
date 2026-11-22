package org.example.sor_mrp.workorder;

import java.time.LocalDateTime;

public record WorkOrderResponseDto(
        Long id,
        String workOrderNumber,
        WorkOrder.Status status,
        Long targetItemId,
        Integer quantity,
        Long assignedTo,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static WorkOrderResponseDto from(WorkOrder entity) {
        return new WorkOrderResponseDto(
                entity.getId(),
                entity.getWorkOrderNumber(),
                entity.getStatus(),
                entity.getTargetItemId(),
                entity.getQuantity(),
                entity.getAssignedTo(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
