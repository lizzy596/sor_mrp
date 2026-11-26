package org.example.sor_mrp.workorder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'getItems')")
    public ResponseEntity<?> getWorkOrders(
            @RequestParam Map<String, String> allParams,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            @RequestParam(required = false) String search) {
        // Separate out pagination/sorting parameters from custom table property filters
        Map<String, String> filters = new HashMap<>(allParams);
        filters.remove("page");
        filters.remove("limit");
        filters.remove("sortBy");
        filters.remove("sortDir");
        filters.remove("search");

        JdbcPaginationHelper.PaginatedResult<WorkOrder> result =
                workOrderService.queryWorkOrders(filters, search, page, limit, sortBy, sortDir);

        // Map entities to safe response DTOs to hide private fields (like passwords)
        // Map entities to safe response DTOs
        List<Object> workOrderResponses =
                result.results().stream().map(WorkOrderResponseDto::fromWorkOrder).toList();

        return ResponseEntity.ok(
                Map.of(
                        "results", workOrderResponses,
                        "page", result.page(),
                        "limit", result.limit(),
                        "totalPages", result.totalPages(),
                        "totalResults", result.totalResults()));
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
