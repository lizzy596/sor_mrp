package org.example.sor_mrp.bom.bom_item;

import jakarta.transaction.Transactional;
import org.example.sor_mrp.bom.BomRepository;
import java.util.List;
import java.util.Optional;

import org.example.sor_mrp.item.Item;
import org.example.sor_mrp.item.UpdateItemRequestDto;
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


    @Transactional
    public List<BomItem> updateBomItemsBatch(BatchUpdateBomItemRequestDto request) {
        // 1. Verify parent BOM exists once
        boolean bomExists = bomRepository.existsByProductNumber(request.getParentItemNumber());
        if (!bomExists) {
            throw new IllegalArgumentException("Parent BOM not found: " + request.getParentItemNumber());
        }

        List<BomItem> processedItems = new java.util.ArrayList<>();

        for (BatchUpdateBomItemRequestDto.ItemDetail detail : request.getItems()) {
            String parentNo = request.getParentItemNumber();
            String childNo = detail.getChildItemNumber();
            java.math.BigDecimal quantity = detail.getQuantity();

            // Find if this item relationship already exists in the database
            java.util.Optional<BomItem> existingItemOpt = bomItemRepository.findByParentItemNumberAndChildItemNumber(parentNo, childNo);

            // Check if quantity is 0 (or less/equal to 0 depending on your exact business rule)
            if (quantity.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                // If it exists, delete it. If it doesn't exist, do nothing.
                existingItemOpt.ifPresent(bomItemRepository::delete);
            } else {
                // Quantity is greater than 0: Update or Create (Upsert)
                BomItem bomItem;
                if (existingItemOpt.isPresent()) {
                    bomItem = existingItemOpt.get();
                    bomItem.setQuantity(quantity);
                } else {
                    bomItem = new BomItem();
                    bomItem.setParentItemNumber(parentNo);
                    bomItem.setChildItemNumber(childNo);
                    bomItem.setQuantity(quantity);
                }
                processedItems.add(bomItemRepository.save(bomItem));
            }
        }

        return processedItems;
    }
    public BomItem updateBomItemById(Long id, UpdateBomItemRequestDto request) {
        // 1. Ensure the BOM item exists before updating
        BomItem existingBomItem =
                bomItemRepository
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("BOM item not found with id: " + id));

        // 2. If the child item number is changing, verify it doesn't already conflict in this BOM context
        if (!existingBomItem.getChildItemNumber().equals(request.getChildItemNumber())) {
            Optional<BomItem> existingMatch = bomItemRepository.findByParentItemNumberAndChildItemNumber(
                    existingBomItem.getParentItemNumber(),
                    request.getParentItemNumber()
            );
            if (existingMatch.isPresent()) {
                throw new IllegalStateException("Item already exists in this BOM: " + request.getChildItemNumber());
            }
        }

        // 3. Update the fields that actually exist on the BomItem entity
        existingBomItem.setChildItemNumber(request.getChildItemNumber());
        existingBomItem.setQuantity(request.getQuantity());

        // 4. Save and return updated entity
        return bomItemRepository.save(existingBomItem);
    }

    public void deleteBomItemById(Long id) {
        if (!bomItemRepository.existsById(id)) {
            throw new IllegalArgumentException("BOM item not found with id: " + id);
        }
        bomItemRepository.deleteById(id);
    }
}
