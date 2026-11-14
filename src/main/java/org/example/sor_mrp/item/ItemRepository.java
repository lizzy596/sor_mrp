package org.example.sor_mrp.item;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

  // Spring Data JPA automatically implements this query from the method name!
  Optional<Item> findByItemNumber(String itemNumber);

  // Note: save(), findById(), and deleteById() are now provided automatically!
}
