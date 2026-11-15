package org.example.sor_mrp.bom.bom_item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BomItemRepository extends JpaRepository<BomItem, Long> {
    // Find all child items belonging to this parent product number
    List<BomItem> findByParentItemNumber(String parentItemNumber);
}
