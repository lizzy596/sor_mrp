package org.example.sor_mrp.bom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boms")
public class BomController {

    private final BomService bomService;

    public BomController(BomService bomService) {
        this.bomService = bomService;
    }

    @PostMapping
    public ResponseEntity<Bom> createBom(@RequestBody Bom bom) {
        Bom savedBom = bomService.save(bom);
        return new ResponseEntity<>(savedBom, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bom> updateBom(@PathVariable Long id, @RequestBody Bom bom) {
        bom.setId(id);
        Bom updatedBom = bomService.save(bom);
        return ResponseEntity.ok(updatedBom);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bom> getBomById(@PathVariable Long id) {
        return bomService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "productNumber")
    public ResponseEntity<List<Bom>> getBomByProductNumber(@RequestParam String productNumber) {
        List<Bom> boms = bomService.findByProductNumber(productNumber);
        return ResponseEntity.ok(boms);
    }

    @GetMapping(params = "productDescription")
    public ResponseEntity<List<Bom>> getBomByProductDescription(@RequestParam String productDescription) {
        List<Bom> boms = bomService.findByProductDescription(productDescription);
        return ResponseEntity.ok(boms);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBom(@PathVariable Long id) {
        bomService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}