package org.example.sor_mrp.bom;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper.PaginatedResult;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcBomRepository implements BomRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final JdbcPaginationHelper paginationHelper;

  public JdbcBomRepository(
      NamedParameterJdbcTemplate jdbcTemplate, JdbcPaginationHelper paginationHelper) {
    this.jdbcTemplate = jdbcTemplate;
    this.paginationHelper = paginationHelper;
  }

  private final RowMapper<Bom> bomRowMapper =
      (rs, rowNum) -> {
        Bom bom = new Bom();
        bom.setId(rs.getLong("id"));
        bom.setParentItemId(rs.getLong("parent_item_id"));
        bom.setChildItemId(rs.getLong("child_item_id"));
        bom.setQuantity(rs.getBigDecimal("quantity"));

        if (rs.getTimestamp("created_at") != null) {
          bom.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
          bom.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return bom;
      };

  @Override
  public Bom save(Bom bom) {
    if (bom.getId() == null) {
      String sql =
          "INSERT INTO bill_of_materials (parent_item_id, child_item_id, quantity) "
              + "VALUES (:parentItemId, :childItemId, :quantity) "
              + "RETURNING id, created_at, updated_at";

      MapSqlParameterSource params =
          new MapSqlParameterSource()
              .addValue("parentItemId", bom.getParentItemId())
              .addValue("childItemId", bom.getChildItemId())
              .addValue("quantity", bom.getQuantity());

      return jdbcTemplate.queryForObject(
          sql,
          params,
          (rs, rowNum) -> {
            bom.setId(rs.getLong("id"));
            if (rs.getTimestamp("created_at") != null) {
              bom.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
              bom.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return bom;
          });
    } else {
      String sql =
          "UPDATE bill_of_materials "
              + "SET quantity = :quantity, updated_at = CURRENT_TIMESTAMP "
              + "WHERE id = :id "
              + "RETURNING updated_at";

      MapSqlParameterSource params =
          new MapSqlParameterSource()
              .addValue("id", bom.getId())
              .addValue("quantity", bom.getQuantity());

      jdbcTemplate.queryForObject(
          sql,
          params,
          (rs, rowNum) -> {
            if (rs.getTimestamp("updated_at") != null) {
              bom.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
            return bom;
          });

      return bom;
    }
  }

  @Override
  public Optional<Bom> findById(Long id) {
    String sql =
        "SELECT id, parent_item_id, child_item_id, quantity, created_at, updated_at "
            + "FROM bill_of_materials WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    List<Bom> results = jdbcTemplate.query(sql, params, bomRowMapper);
    return results.stream().findFirst();
  }

  @Override
  public List<Bom> findByParentItemId(Long parentItemId) {
    String sql =
        "SELECT id, parent_item_id, child_item_id, quantity, created_at, updated_at "
            + "FROM bill_of_materials WHERE parent_item_id = :parentItemId";
    MapSqlParameterSource params =
        new MapSqlParameterSource().addValue("parentItemId", parentItemId);
    return jdbcTemplate.query(sql, params, bomRowMapper);
  }

  @Override
  public List<Bom> findByChildItemId(Long childItemId) {
    String sql =
        "SELECT id, parent_item_id, child_item_id, quantity, created_at, updated_at "
            + "FROM bill_of_materials WHERE child_item_id = :childItemId";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("childItemId", childItemId);
    return jdbcTemplate.query(sql, params, bomRowMapper);
  }

  @Override
  public void deleteById(Long id) {
    String sql = "DELETE FROM bill_of_materials WHERE id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public boolean existsByParentAndChild(Long parentItemId, Long childItemId) {
    String sql =
        "SELECT COUNT(*) FROM bill_of_materials "
            + "WHERE parent_item_id = :parentItemId AND child_item_id = :childItemId";
    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("parentItemId", parentItemId)
            .addValue("childItemId", childItemId);
    Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
    return count != null && count > 0;
  }

  @Override
  public PaginatedResult<BomDetail> findDetailedPaginated(
      int page,
      int size,
      String sortBy,
      String sortDir,
      String search,
      Map<String, String> filters) {

    StringBuilder baseSql =
        new StringBuilder(
            "FROM bill_of_materials b "
                + "JOIN items p ON b.parent_item_id = p.id "
                + "JOIN items c ON b.child_item_id = c.id "
                + "WHERE 1=1");

    MapSqlParameterSource params = new MapSqlParameterSource();

    // 1. Global text search across parent/child item numbers and descriptions
    if (search != null && !search.isBlank()) {
      baseSql
          .append(" AND (")
          .append("LOWER(p.item_number) LIKE :searchPattern OR ")
          .append("LOWER(p.description) LIKE :searchPattern OR ")
          .append("LOWER(c.item_number) LIKE :searchPattern OR ")
          .append("LOWER(c.description) LIKE :searchPattern")
          .append(")");
      params.addValue("searchPattern", "%" + search.toLowerCase() + "%");
    }

    // 2. Dynamic property filters (e.g., filtering by exact parent ID or item type)
    if (filters != null) {
      int i = 0;
      for (Map.Entry<String, String> entry : filters.entrySet()) {
        String field = entry.getKey();
        String value = entry.getValue();
        if (value != null && !value.isBlank()) {
          String paramName = "filter_" + i;
          // Whitelist allowed filter columns to prevent SQL injection
          if (field.equals("parent_item_id") || field.equals("child_item_id")) {
            baseSql.append(" AND b.").append(field).append(" = :").append(paramName);
            params.addValue("paramName", Long.valueOf(value));
            i++;
          }
        }
      }
    }

    // 3. Count total matching rows for pagination metadata
    String countSql = "SELECT COUNT(*) " + baseSql.toString();
    Long totalResults = jdbcTemplate.queryForObject(countSql, params, Long.class);
    if (totalResults == null) totalResults = 0L;

    // 4. Sorting & Whitelisting sort columns
    String orderByColumn =
        switch (sortBy) {
          case "parentItemNumber" -> "p.item_number";
          case "childItemNumber" -> "c.item_number";
          case "quantity" -> "b.quantity";
          case "updatedAt" -> "b.updated_at";
          default -> "b.id";
        };

    String direction = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
    baseSql.append(" ORDER BY ").append(orderByColumn).append(" ").append(direction);
    baseSql.append(" LIMIT :limit OFFSET :offset");
    params.addValue("limit", size);
    params.addValue("offset", (long) page * size);

    // 5. Select query with all joined details
    String selectSql =
        "SELECT b.id, b.parent_item_id, p.item_number AS parent_item_number, p.description AS parent_description, "
            + "b.child_item_id, c.item_number AS child_item_number, c.description AS child_description, "
            + "b.quantity, b.created_at, b.updated_at "
            + baseSql.toString();

    RowMapper<BomDetail> mapper =
        (rs, rowNum) ->
            new BomDetail(
                rs.getLong("id"),
                rs.getLong("parent_item_id"),
                rs.getString("parent_item_number"),
                rs.getString("parent_description"),
                rs.getLong("child_item_id"),
                rs.getString("child_item_number"),
                rs.getString("child_description"),
                rs.getBigDecimal("quantity"),
                rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime()
                    : null,
                rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime()
                    : null);

    List<BomDetail> results = jdbcTemplate.query(selectSql, params, mapper);
    int totalPages = (int) Math.ceil((double) totalResults / size);

    return new PaginatedResult<>(results, totalResults, totalPages, page + 1, size);
  }
}
