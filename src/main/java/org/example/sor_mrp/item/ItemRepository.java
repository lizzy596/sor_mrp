package org.example.sor_mrp.item;

import java.util.Map;
import java.util.Optional;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

public interface ItemRepository {
  Item save(Item item);

  Optional<Item> findById(Long id);

  Optional<Item> findByItemNumber(String itemNumber);

  Item update(Long id, Item item);

  void deleteById(Long id);

  JdbcPaginationHelper.PaginatedResult<Item> findAllFiltered(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir);
}
