package org.example.sor_mrp.bom;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BomService {

    private final BomRepository bomRepository;

    public BomService(BomRepository bomRepository) {
        this.bomRepository = bomRepository;
    }

    public Bom save(Bom bom) {
        // You can add additional validation or business logic here before saving
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

    public void deleteById(Long id) {
        bomRepository.deleteById(id);
    }

    public boolean existsByProductNumberandDescription(String productNumber, String description) {
        // Fixed capitalization for 'And' so Spring Data JPA recognizes the keyword
        return bomRepository.existsByProductNumberAndProductDescription(productNumber, description);
    }
}
