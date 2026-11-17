package org.example.sor_mrp.bom.bom_item;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bomitems")
public class BomItemController {

    private final BomItemService bomItemService;

    public BomItemController(BomItemService bomItemService) {
        this.bomItemService = bomItemService;
    }

    @PostMapping("/item")
    public ResponseEntity<BomItem> createBomItem(@Valid @RequestBody CreateBomItemRequestDto request) {

        BomItem savedBomItem = bomItemService.createBomItem(request);
        return new ResponseEntity<>(savedBomItem, HttpStatus.CREATED);
    }


    @PostMapping("/batch")
    public ResponseEntity<List<BomItem>> createBomItemsBatch(@Valid @RequestBody BatchCreateBomItemRequestDto request) {
        List<BomItem> savedItems = bomItemService.createBomItemsBatch(request);
        return new ResponseEntity<>(savedItems, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")

    public ResponseEntity<BomItemResponseDto> updateBomItemById(
            @PathVariable Long id, @Valid @RequestBody UpdateBomItemRequestDto request) {
        BomItem updatedBomItem = bomItemService.updateBomItemById(id, request);
        BomItemResponseDto responseDto = BomItemResponseDto.fromBomItem(updatedBomItem);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBomItemById(@PathVariable Long id) {
        bomItemService.deleteBomItemById(id);
        return ResponseEntity.noContent().build();
    }
}
