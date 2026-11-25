package org.example.sor_mrp.workorder.workorder_reconciliation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderReconciliationRepository
        extends JpaRepository<WorkOrderReconciliation, Long> {

    List<WorkOrderReconciliation> findAllByWorkOrderNumber(String workOrderNumber);

    boolean existsByWorkOrderNumber(String workOrderNumber);

    List<WorkOrderReconciliation> findAllByTargetItemId(Long targetItemId);

    List<WorkOrderReconciliation> findAllByComponentItemId(Long componentItemId);

    Optional<WorkOrderReconciliation> findByWorkOrderNumberAndComponentItemId(
            String workOrderNumber, Long componentItemId);

    List<WorkOrderReconciliation> findAllByCompletedBy(Long completedBy);
}