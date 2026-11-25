package org.example.sor_mrp.workorder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    public record UpdateStatusRequest(
            WorkOrder.Status status
    ) {}

    @GetMapping
    public ResponseEntity<List<WorkOrderResponseDto>> getAll() {
        return ResponseEntity.ok(workOrderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<WorkOrderResponseDto> create(
            @Valid @RequestBody CreateWorkOrderRequestDto request) {
        Long createdBy = resolveCurrentUserId();
        WorkOrderResponseDto created = workOrderService.create(request, createdBy);
        return ResponseEntity
                .created(URI.create("/api/work-orders/" + created.id()))
                .body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkOrderResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkOrderRequestDto request) {
        return ResponseEntity.ok(workOrderService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(workOrderService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long resolveCurrentUserId() {
        // Replace with your authenticated user lookup from SecurityContext / principal
        return 1L;
    }
}
