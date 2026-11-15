package org.example.sor_mrp.bom.bom_item;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{bomId}/item")
    public ResponseEntity<BomItem> createBomItem(@PathVariable Long bomId,
                                                 @Valid @RequestBody CreateBomItemRequestDto request) {

        BomItem savedBomItem = bomItemService.createBomItem(request);
        return new ResponseEntity<>(savedBomItem, HttpStatus.CREATED);
    }


    @PostMapping("/{bomId}/batch")
    public ResponseEntity<List<BomItem>> createBomItemsBatch(@Valid @RequestBody BatchCreateBomItemRequestDto request) {
        List<BomItem> savedItems = bomItemService.createBomItemsBatch(request);
        return new ResponseEntity<>(savedItems, HttpStatus.CREATED);
    }
}
