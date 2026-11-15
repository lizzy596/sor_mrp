package org.example.sor_mrp.bom;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class BomService {

  private final BomRepository bomRepository;

  public BomService(BomRepository bomRepository) {
    this.bomRepository = bomRepository;
  }

  public Bom createBom(CreateBomRequestDto request) {
    // 1. Map the DTO fields to a new Bom entity
    Bom bom = new Bom();
    bom.setProductNumber(request.getProductNumber());
    bom.setProductDescription(request.getProductDescription());

    // 2. Save and return the entity
    return bomRepository.save(bom);
  }

  public Optional<Bom> findById(Long id) {
    return bomRepository.findById(id);
  }

  public List<Bom> findByProductNumber(String productNumber) {
    return bomRepository.findByProductNumber(productNumber);
  }

  public List<Bom> findByProductDescription(String productDescription) {
    // Updated to match the JPA repository query method
    return bomRepository.findByProductDescriptionContainingIgnoreCase(productDescription);
  }

  public Bom updateBomById(Long id, UpdateBomRequestDto request) {
    // 1. Find the existing BOM or throw an exception
    Bom existingBom =
            bomRepository
                    .findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Bom not found with id: " + id));

    // 2. If the product number is changing, check if the new number is already taken
    if (!existingBom.getProductNumber().equals(request.getProductNumber())) {
      List<Bom> existingWithNumber = bomRepository.findByProductNumber(request.getProductNumber());
      if (!existingWithNumber.isEmpty()) {
        throw new IllegalStateException("Bom with product number already exists: " + request.getProductNumber());
      }
    }

    // 3. Update fields using the UpdateBomRequestDto getters
    existingBom.setProductNumber(request.getProductNumber());
    existingBom.setProductDescription(request.getProductDescription());

    // 4. Save and return the updated entity
    return bomRepository.save(existingBom);
  }

  public void deleteById(Long id) {
    bomRepository.deleteById(id);
  }

  public boolean existsByProductNumberandDescription(String productNumber, String description) {
    // Fixed capitalization for 'And' so Spring Data JPA recognizes the keyword
    return bomRepository.existsByProductNumberAndProductDescription(productNumber, description);
  }
}
