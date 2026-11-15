package org.example.sor_mrp.bom;

import java.util.List;
import jakarta.validation.Valid;
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

  @GetMapping("/{id}")
  public ResponseEntity<Bom> getBomById(@PathVariable Long id) {
    return bomService
            .findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
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
