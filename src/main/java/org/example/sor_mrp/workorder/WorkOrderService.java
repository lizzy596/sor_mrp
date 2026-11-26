package org.example.sor_mrp.workorder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.example.sor_mrp.bom.bom_item.BomItem;
import org.example.sor_mrp.item.Item;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.example.sor_mrp.workorder.workorder_reconciliation.WorkOrderReconciliation;
import org.example.sor_mrp.workorder.workorder_reconciliation.WorkOrderReconciliationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.sor_mrp.item.ItemRepository;
import org.example.sor_mrp.bom.bom_item.BomItemRepository;

@Service
@Transactional(readOnly = true)
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderReconciliationRepository workOrderReconciliationRepository;

    private final ItemRepository itemRepository;
    private final BomItemRepository bomItemRepository;


    public WorkOrderService(WorkOrderRepository workOrderRepository, ItemRepository itemRepository, BomItemRepository bomItemRepository, WorkOrderReconciliationRepository workOrderReconciliationRepository) {
        this.workOrderRepository = workOrderRepository;
        this.workOrderReconciliationRepository = workOrderReconciliationRepository;
        this.itemRepository = itemRepository;
        this.bomItemRepository = bomItemRepository;
    }

    public List<WorkOrderResponseDto> findAll() {
        return workOrderRepository.findAll()
                .stream()
                .map(WorkOrderResponseDto::from)
                .toList();
    }

    public WorkOrderResponseDto findById(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("WorkOrder not found: " + id));
        return WorkOrderResponseDto.from(workOrder);
    }

    @Transactional
    public WorkOrderResponseDto create(CreateWorkOrderRequestDto request, Long createdBy) {
        Item targetItem = itemRepository.findById(request.targetItemId())
                .orElseThrow(() -> new NoSuchElementException("Item not found: " + request.targetItemId()));

        WorkOrder entity = new WorkOrder(
                request.workOrderNumber(),
                request.status() != null ? request.status() : WorkOrder.Status.PLANNED,
                request.targetItemId(),
                request.quantity(),
                request.assignedTo(),
                createdBy,
                request.dueBy()
        );
        WorkOrder saved = workOrderRepository.save(entity);

        // Direct BOM children only; subassembly (COMPONENT) items remain single lines without explosion
        List<BomItem> bomItems =
                bomItemRepository.findByParentItemNumber(targetItem.getItemNumber());

        BigDecimal orderQty = BigDecimal.valueOf(saved.getQuantity());
        List<WorkOrderReconciliation> rows = bomItems.stream()
                .map(bomItem -> {
                    Item componentItem = itemRepository.findByItemNumber(bomItem.getChildItemNumber())
                            .orElseThrow(() -> new NoSuchElementException(
                                    "Component item not found: " + bomItem.getChildItemNumber()));
                    BigDecimal expectedQty = bomItem.getQuantity().multiply(orderQty);
                    return new WorkOrderReconciliation(
                            saved.getWorkOrderNumber(),
                            saved.getTargetItemId(),
                            componentItem.getId(),
                            expectedQty
                    );
                })
                .toList();

        workOrderReconciliationRepository.saveAll(rows);

        return WorkOrderResponseDto.from(saved);
    }

    @Transactional
    public WorkOrderResponseDto update(Long id, UpdateWorkOrderRequestDto request) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("WorkOrder not found: " + id));

        if (request.assignedTo() != null) {
            workOrder.setAssignedTo(request.assignedTo());
        }
        if (request.status() != null) {
            workOrder.setStatus(request.status());
        }
        if (request.quantity() != null) {
            workOrder.setQuantity(request.quantity());
        }
        if (request.dueBy() != null) {
            workOrder.setDueBy(request.dueBy());
        }

        return WorkOrderResponseDto.from(workOrder);
    }

    @Transactional
    public WorkOrderResponseDto updateStatus(Long id, WorkOrder.Status status) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("WorkOrder not found: " + id));
        workOrder.setStatus(status);
        return WorkOrderResponseDto.from(workOrder);
    }

    @Transactional
    public void delete(Long id) {
        if (!workOrderRepository.existsById(id)) {
            throw new NoSuchElementException("WorkOrder not found: " + id);
        }
        workOrderRepository.deleteById(id);
    }

    public JdbcPaginationHelper.PaginatedResult<WorkOrder> queryWorkOrders(
            Map<String, String> filters,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        // Adjust for 0-indexed pages if client sends 1-indexed (e.g., page=1 becomes 0)
        int pageIndex = Math.max(0, page - 1);
        return workOrderRepository.findAllFiltered(filters, search, pageIndex, size, sortBy, sortDir);
    }
}
