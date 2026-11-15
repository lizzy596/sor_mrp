package org.example.sor_mrp.bom.bom_item;

import jakarta.transaction.Transactional;
import org.example.sor_mrp.bom.BomRepository;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BomItemService {
    private final BomItemRepository bomItemRepository;
    private final BomRepository bomRepository;

    public BomItemService(BomItemRepository bomItemRepository, BomRepository bomRepository) {
        this.bomItemRepository = bomItemRepository;
        this.bomRepository = bomRepository;
    }

    public BomItem createBomItem(CreateBomItemRequestDto request) {
        boolean bomExists = bomRepository.existsByProductNumber(request.getParentItemNumber());

        if (!bomExists) {
            // Caught by GlobalExceptionHandler -> returns 400 Bad Request
            throw new IllegalArgumentException("Parent BOM does not exist: " + request.getParentItemNumber());
        }

        BomItem bomitem = new BomItem();
        bomitem.setParentItemNumber(request.getParentItemNumber());
        bomitem.setChildItemNumber(request.getChildItemNumber());
        bomitem.setQuantity(request.getQuantity());

        return bomItemRepository.save(bomitem);
    }

    @Transactional
    public List<BomItem> createBomItemsBatch(BatchCreateBomItemRequestDto request) {
        // 1. Verify parent BOM exists once
        boolean bomExists = bomRepository.existsByProductNumber(request.getParentItemNumber());
        if (!bomExists) {
            // Caught by GlobalExceptionHandler -> returns 400 Bad Request
            throw new IllegalArgumentException("Parent BOM not found: " + request.getParentItemNumber());
        }

        // 2. Map each item detail into a BomItem entity
        List<BomItem> bomItems = request.getItems().stream().map(detail -> {
            BomItem item = new BomItem();
            item.setParentItemNumber(request.getParentItemNumber());
            item.setChildItemNumber(detail.getChildItemNumber());
            item.setQuantity(detail.getQuantity());
            return item;
        }).toList();

        // 3. Save all items.
        // If a duplicate unique constraint hits, DataIntegrityViolationException is thrown
        // and caught automatically by your GlobalExceptionHandler -> returns 409 Conflict.
        return bomItemRepository.saveAll(bomItems);
    }
}
