package org.example.sor_mrp.item;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcItemRepository implements ItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcItemRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Item> itemRowMapper = (rs, rowNum) -> {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setItemNumber(rs.getString("item_number"));

        String itemTypeStr = rs.getString("item_type");
        if (itemTypeStr != null) {
            item.setItemType(ItemType.valueOf(itemTypeStr));
        }

        item.setQuantity(rs.getInt("quantity"));
        item.setDescription(rs.getString("description"));

        if (rs.getTimestamp("created_at") != null) {
            item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return item;
    };

    @Override
    public Item save(Item item) {
        String sql = """
            INSERT INTO items (item_number, item_type, quantity, description)
            VALUES (:itemNumber, :itemType, :quantity, :description)
            RETURNING id, created_at, updated_at
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("itemNumber", item.getItemNumber())
                .addValue("itemType", item.getItemType() != null ? item.getItemType().name() : ItemType.RAW_MATERIAL.name())
                .addValue("quantity", item.getQuantity())
                .addValue("description", item.getDescription());

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            item.setId(rs.getLong("id"));
            if (rs.getTimestamp("created_at") != null) {
                item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
                item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return item;
        });
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "SELECT id, item_number, item_type, quantity, description, created_at, updated_at FROM items WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

        List<Item> results = jdbcTemplate.query(sql, params, itemRowMapper);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Item> findByItemNumber(String itemNumber) {
        String sql = "SELECT id, item_number, item_type, quantity, description, created_at, updated_at FROM items WHERE item_number = :itemNumber";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("itemNumber", itemNumber);

        List<Item> results = jdbcTemplate.query(sql, params, itemRowMapper);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Item update(Long id, Item item) {
        String sql = """
            UPDATE items 
            SET item_number = :itemNumber, item_type = :itemType, quantity = :quantity, description = :description, updated_at = CURRENT_TIMESTAMP
            WHERE id = :id
            RETURNING id, item_number, item_type, quantity, description, created_at, updated_at
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("itemNumber", item.getItemNumber())
                .addValue("itemType", item.getItemType() != null ? item.getItemType().name() : ItemType.RAW_MATERIAL.name())
                .addValue("quantity", item.getQuantity())
                .addValue("description", item.getDescription());

        return jdbcTemplate.queryForObject(sql, params, itemRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM items WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public JdbcPaginationHelper.PaginatedResult<Item> findAllFiltered(
            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
    ) {
        List<String> allowedColumns = List.of("id", "item_number", "item_type", "quantity", "description", "created_at", "updated_at");
        List<String> searchableColumns = List.of("item_number");

        return JdbcPaginationHelper.paginate(
                jdbcTemplate,
                "items",
                filters,
                search,
                searchableColumns,
                allowedColumns,
                page,
                size,
                sortBy,
                sortDir,
                itemRowMapper
        );
    }
}
