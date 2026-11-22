package org.example.sor_mrp.workorder;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
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
    public WorkOrderResponseDto create(
            String workOrderNumber,
            WorkOrder.Status status,
            Long targetItemId,
            Integer quantity,
            Long assignedTo,
            Long createdBy) {
        WorkOrder entity = new WorkOrder(
                workOrderNumber,
                status != null ? status : WorkOrder.Status.PLANNED,
                targetItemId,
                quantity,
                assignedTo,
                createdBy
        );
        WorkOrder saved = workOrderRepository.save(entity);
        return WorkOrderResponseDto.from(saved);
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
}
