package org.example.sor_mrp.item;

import java.util.List;
import java.util.Map;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepositoryImpl implements ItemRepositoryCustom {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final JdbcPaginationHelper paginationHelper; // Injected component

  // Define allowed columns for filtering/sorting and searchable columns for global search
  private static final List<String> ALLOWED_COLUMNS =
      List.of(
          "id", "item_number", "quantity", "description", "item_type", "created_at", "updated_at");

  private static final List<String> SEARCHABLE_COLUMNS = List.of("item_number", "description");

  public ItemRepositoryImpl(
      NamedParameterJdbcTemplate jdbcTemplate, JdbcPaginationHelper paginationHelper) {
    this.jdbcTemplate = jdbcTemplate;
    this.paginationHelper = paginationHelper;
  }

  @Override
  public JdbcPaginationHelper.PaginatedResult<Item> findAllFiltered(
      Map<String, String> filters,
      String search,
      int page,
      int size,
      String sortBy,
      String sortDir) {

    // Call the static paginate method on your helper
    return JdbcPaginationHelper.paginate(
        jdbcTemplate,
        "items", // Table name
        filters,
        search,
        SEARCHABLE_COLUMNS,
        ALLOWED_COLUMNS,
        page,
        size,
        sortBy,
        sortDir,
        (rs, rowNum) -> {
          Item item = new Item();
          item.setId(rs.getLong("id"));
          item.setItemNumber(rs.getString("item_number"));
          item.setQuantity(rs.getInt("quantity"));
          item.setDescription(rs.getString("description"));

          if (rs.getString("item_type") != null) {
            item.setItemType(ItemType.valueOf(rs.getString("item_type")));
          }

          if (rs.getTimestamp("created_at") != null) {
            item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
          }
          if (rs.getTimestamp("updated_at") != null) {
            item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
          }
          return item;
        });
  }
}
