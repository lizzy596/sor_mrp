package org.example.sor_mrp.bom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.example.sor_mrp.item.Item;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boms")
public class BomController {

  private final BomService bomService;

  public BomController(BomService bomService) {
    this.bomService = bomService;
  }

  @PostMapping
  public ResponseEntity<Bom> createBom(@Valid @RequestBody CreateBomRequestDto request) {
    // FIXED: Pass 'request' (the DTO) instead of the undefined 'bom' variable
    Bom savedBom = bomService.createBom(request);
    return new ResponseEntity<>(savedBom, HttpStatus.CREATED);
  }

  // GET /api/boms?role=user&search=john&page=1&limit=10&sortBy=created_at&sortDir=DESC
  @GetMapping
  //@PreAuthorize("@securityEvaluator.hasAccess(authentication, null, 'getItems')")
  public ResponseEntity<?> getBoms(
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

    JdbcPaginationHelper.PaginatedResult<Bom> result =
            bomService.queryBoms(filters, search, page, limit, sortBy, sortDir);

    // Map entities to safe response DTOs to hide private fields (like passwords)
    // Map entities to safe response DTOs
    List<BomResponseDto> bomResponses = result.results().stream()
            .map(BomResponseDto::fromBom)
            .toList();

    return ResponseEntity.ok(
            Map.of(
                    "results", bomResponses,
                    "page", result.page(),
                    "limit", result.limit(),
                    "totalPages", result.totalPages(),
                    "totalResults", result.totalResults()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BomWithItemsResponseDto> getBomById(@PathVariable Long id) {
    BomWithItemsResponseDto response = bomService.getBomWithItems(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping(params = "productNumber")
  public ResponseEntity<List<Bom>> getBomByProductNumber(@RequestParam String productNumber) {
    List<Bom> boms = bomService.findByProductNumber(productNumber);
    return ResponseEntity.ok(boms);
  }

  @GetMapping(params = "productDescription")
  public ResponseEntity<List<Bom>> getBomByProductDescription(
          @RequestParam String productDescription) {
    List<Bom> boms = bomService.findByProductDescription(productDescription);
    return ResponseEntity.ok(boms);
  }

  @PutMapping("/{id}")
  public ResponseEntity<BomResponseDto> updateBomById(
          @PathVariable Long id, @Valid @RequestBody UpdateBomRequestDto request) {

    // 1. Call the service to update the BOM entity
    Bom updatedBom = bomService.updateBomById(id, request);

    // 2. Map the updated BOM entity to a BomResponseDto using the correct factory method
    BomResponseDto responseDto = BomResponseDto.fromBom(updatedBom);

    // 3. Return the clean response DTO with a 200 OK status
    return ResponseEntity.ok(responseDto);
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBom(@PathVariable Long id) {
    bomService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
