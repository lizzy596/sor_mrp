package org.example.sor_mrp.bom;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.sor_mrp.bom.bom_item.BomItem;
import org.example.sor_mrp.bom.bom_item.BomItemRepository;
import org.example.sor_mrp.bom.bom_item.BomItemService;
import org.example.sor_mrp.item.Item;
import org.example.sor_mrp.shared.configuration.exception.ResourceNotFoundException;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.stereotype.Service;

@Service
public class BomService {

  private final BomRepository bomRepository;
  private final BomItemRepository bomItemRepository;



  public BomService(BomRepository bomRepository,  BomItemRepository bomItemRepository) {
    this.bomRepository = bomRepository;
    this.bomItemRepository = bomItemRepository;
  }

  public Bom createBom(CreateBomRequestDto request) {
    // 1. Map the DTO fields to a new Bom entity
    Bom bom = new Bom();
    bom.setProductNumber(request.getProductNumber());
    bom.setProductDescription(request.getProductDescription());

    // 2. Save and return the entity
    return bomRepository.save(bom);
  }

  public BomWithItemsResponseDto getBomWithItems(Long id) {
    // 1. Find the BOM by ID, or throw your custom 404 exception if it doesn't exist
    Bom bom = bomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("BOM not found with id: " + id));

    // 2. Fetch all associated bom items using the product number string
    List<BomItem> items = bomItemRepository.findByParentItemNumber(bom.getProductNumber());

    // 3. Return combined response
    return new BomWithItemsResponseDto(bom, items);
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

  public JdbcPaginationHelper.PaginatedResult<Bom> queryBoms(
          Map<String, String> filters,
          String search,
          int page,
          int size,
          String sortBy,
          String sortDir) {
    // Adjust for 0-indexed pages if client sends 1-indexed (e.g., page=1 becomes 0)
    int pageIndex = Math.max(0, page - 1);
    return bomRepository.findAllFiltered(filters, search, pageIndex, size, sortBy, sortDir);
  }
}
