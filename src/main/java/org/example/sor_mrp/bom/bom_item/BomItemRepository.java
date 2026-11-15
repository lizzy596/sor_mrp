package org.example.sor_mrp.bom.bom_item;
import java.util.List;

import org.example.sor_mrp.bom.bom_item.BomItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BomItemRepository extends JpaRepository<BomItem, Long> {
}
