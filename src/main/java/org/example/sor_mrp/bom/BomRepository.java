package org.example.sor_mrp.bom;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {

  // Spring Data JPA automatically generates queries from method names!
  List<Bom> findByProductNumber(String productNumber);

  List<Bom> findByProductDescriptionContainingIgnoreCase(String productDescription);

  boolean existsByProductNumberAndProductDescription(
      String productNumber, String productDescription);
}
