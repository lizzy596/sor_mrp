package org.example.sor_mrp.item;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.sor_mrp.bom.BomService;
import org.example.sor_mrp.bom.CreateBomRequestDto;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

  private final ItemService itemService;
  private final BomService bomService;

  public ItemController(ItemService itemService, BomService bomService) {

    this.itemService = itemService;
    this.bomService = bomService;
  }

  @PostMapping
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'manageItems')")
  public ResponseEntity<Item> createItem(@Valid @RequestBody CreateItemRequestDto request) {
    Item createdItem = itemService.createItem(request);

    if (request.getItemType() == ItemType.COMPONENT || request.getItemType() == ItemType.FINAL) {
      CreateBomRequestDto bomRequest = new CreateBomRequestDto();
      bomRequest.setProductNumber(createdItem.getItemNumber());
      bomRequest.setProductDescription(createdItem.getDescription());
      bomService.createBom(bomRequest);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
  }

  // GET /api/items?role=user&search=john&page=1&limit=10&sortBy=created_at&sortDir=DESC
  @GetMapping
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'getItems')")
  public ResponseEntity<?> getItems(
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

    JdbcPaginationHelper.PaginatedResult<Item> result =
        itemService.queryItems(filters, search, page, limit, sortBy, sortDir);

    // Map entities to safe response DTOs to hide private fields (like passwords)
    // Map entities to safe response DTOs
    List<ItemResponseDto> itemResponses =
        result.results().stream().map(ItemResponseDto::fromItem).toList();

    return ResponseEntity.ok(
        Map.of(
            "results", itemResponses,
            "page", result.page(),
            "limit", result.limit(),
            "totalPages", result.totalPages(),
            "totalResults", result.totalResults()));
  }

  @GetMapping("/{id}")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, #id.toString(), 'getItems')")
  public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id) {
    return itemService
        .getItemById(id)
        .map(ItemResponseDto::fromItem) // Map the Item entity to ItemResponseDto if found
        .map(ResponseEntity::ok) // Wrap in a 200 OK response
        .orElse(ResponseEntity.notFound().build()); // Return 404 Not Found if missing
  }

  @PutMapping("/{id}")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, #id.toString(), 'manageItems')")
  public ResponseEntity<ItemResponseDto> updateItemById(
      @PathVariable Long id, @Valid @RequestBody UpdateItemRequestDto request) {
    Item updatedItem = itemService.updateItemById(id, request);
    ItemResponseDto responseDto = ItemResponseDto.fromItem(updatedItem);
    return ResponseEntity.ok(responseDto);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'deleteItem')")
  public ResponseEntity<Void> deleteItemById(@PathVariable Long id) {
    itemService.deleteItemById(id);
    return ResponseEntity.noContent().build();
  }
}
