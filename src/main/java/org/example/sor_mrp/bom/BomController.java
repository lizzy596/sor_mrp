package org.example.sor_mrp.bom;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper.PaginatedResult;
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
  @PreAuthorize("hasAuthority('manageBoms')")
  public ResponseEntity<BomResponseDto> createBom(
      @Valid @RequestBody CreateBomRequestDto requestDto) {
    Bom createdBom = bomService.createBom(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(new BomResponseDto(createdBom));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('manageBoms')")
  public ResponseEntity<BomResponseDto> updateBom(
      @PathVariable Long id, @Valid @RequestBody UpdateBomRequestDto requestDto) {
    Bom updatedBom = bomService.updateBom(id, requestDto);
    return ResponseEntity.ok(new BomResponseDto(updatedBom));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('manageBoms')")
  public ResponseEntity<Void> deleteBom(@PathVariable Long id) {
    bomService.deleteBom(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('getBoms')")
  public ResponseEntity<BomResponseDto> getBomById(@PathVariable Long id) {
    Bom bom = bomService.findById(id);
    return ResponseEntity.ok(new BomResponseDto(bom));
  }

  @GetMapping("/parent/{parentItemId}")
  @PreAuthorize("hasAuthority('getBoms')")
  public ResponseEntity<List<BomResponseDto>> getComponentsByParentId(
      @PathVariable Long parentItemId) {
    List<BomResponseDto> responseDtos =
        bomService.findComponentsByParentId(parentItemId).stream()
            .map(BomResponseDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(responseDtos);
  }

  @GetMapping("/child/{childItemId}")
  @PreAuthorize("hasAuthority('getBoms')")
  public ResponseEntity<List<BomResponseDto>> getWhereUsedByChildId(
      @PathVariable Long childItemId) {
    List<BomResponseDto> responseDtos =
        bomService.findWhereUsedByChildId(childItemId).stream()
            .map(BomResponseDto::new)
            .collect(Collectors.toList());
    return ResponseEntity.ok(responseDtos);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('getBoms')")
  public ResponseEntity<PaginatedResult<BomDetail>> getPaginatedBoms(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "ASC") String sortDir,
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Map<String, String> filters) {

    PaginatedResult<BomDetail> response =
        bomService.getPaginatedBoms(page, size, sortBy, sortDir, search, filters);

    return ResponseEntity.ok(response);
  }
}
