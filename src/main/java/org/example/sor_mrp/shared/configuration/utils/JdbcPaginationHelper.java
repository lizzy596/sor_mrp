package org.example.sor_mrp.shared.configuration.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcPaginationHelper {

  public static <T> PaginatedResult<T> paginate(
      NamedParameterJdbcTemplate jdbcTemplate,
      String tableName,
      Map<String, String> filters,
      String search,
      List<String> searchableColumns,
      List<String> allowedColumns,
      int page,
      int size,
      String sortBy,
      String sortDir,
      RowMapper<T> rowMapper) {
    StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
    StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " WHERE 1=1");
    MapSqlParameterSource params = new MapSqlParameterSource();

    // 1. Dynamic property filters (e.g., role = 'user')
    if (filters != null) {
      int i = 0;
      for (Map.Entry<String, String> entry : filters.entrySet()) {
        String field = entry.getKey();
        String value = entry.getValue();
        if (value != null && !value.isBlank() && allowedColumns.contains(field)) {
          String paramName = "filter_" + i;
          sql.append(" AND ").append(field).append(" = :").append(paramName);
          countSql.append(" AND ").append(field).append(" = :").append(paramName);
          params.addValue(paramName, value);
          i++;
        }
      }
    }

    // 2. Global search across searchable columns
    if (search != null
        && !search.isBlank()
        && searchableColumns != null
        && !searchableColumns.isEmpty()) {
      sql.append(" AND (");
      countSql.append(" AND (");
      List<String> searchClauses = new ArrayList<>();
      String searchPattern = "%" + search.toLowerCase() + "%";

      int j = 0;
      for (String col : searchableColumns) {
        if (allowedColumns.contains(col)) {
          String paramName = "search_" + j;
          searchClauses.add("LOWER(" + col + ") LIKE :" + paramName);
          params.addValue(paramName, searchPattern);
          j++;
        }
      }

      sql.append(String.join(" OR ", searchClauses));
      countSql.append(String.join(" OR ", searchClauses));
      sql.append(")");
      countSql.append(")");
    }

    // 3. Get total count
    Long totalResults = jdbcTemplate.queryForObject(countSql.toString(), params, Long.class);
    if (totalResults == null) totalResults = 0L;

    // 4. Sorting & Pagination (LIMIT / OFFSET)
    if (sortBy != null && allowedColumns.contains(sortBy)) {
      String direction = "DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
      sql.append(" ORDER BY ").append(sortBy).append(" ").append(direction);
    } else {
      sql.append(" ORDER BY id ASC"); // Fallback default sorting
    }

    sql.append(" LIMIT :limit OFFSET :offset");
    params.addValue("limit", size);
    params.addValue("offset", page * size);

    // 5. Execute query
    List<T> results = jdbcTemplate.query(sql.toString(), params, rowMapper);

    int totalPages = (int) Math.ceil((double) totalResults / size);

    return new PaginatedResult<>(results, totalResults, totalPages, page + 1, size);
  }

  public record PaginatedResult<T>(
      List<T> results, long totalResults, int totalPages, int page, int limit) {}
}
// package org.example.sor_mrp.utils;
//
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.RowMapper;
// import org.springframework.stereotype.Component;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
//
// @Component
// public class JdbcPaginationHelper {
//
//    public static <T> PaginatedResult<T> paginate(
//            JdbcTemplate jdbcTemplate,
//            String tableName,
//            Map<String, String> filters,
//            String search,
//            List<String> searchableColumns,
//            List<String> allowedColumns,
//            int page,
//            int size,
//            String sortBy,
//            String sortDir,
//            RowMapper<T> rowMapper
//    ) {
//        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
//        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " WHERE
// 1=1");
//        List<Object> params = new ArrayList<>();
//
//        // 1. Dynamic property filters (e.g., role = 'user')
//        if (filters != null) {
//            filters.forEach((field, value) -> {
//                if (value != null && !value.isBlank() && allowedColumns.contains(field)) {
//                    sql.append(" AND ").append(field).append(" = ?");
//                    countSql.append(" AND ").append(field).append(" = ?");
//                    params.add(value);
//                }
//            });
//        }
//
//        // 2. Global search across searchable columns
//        if (search != null && !search.isBlank() && searchableColumns != null &&
// !searchableColumns.isEmpty()) {
//            sql.append(" AND (");
//            countSql.append(" AND (");
//            List<String> searchClauses = new ArrayList<>();
//            String searchPattern = "%" + search.toLowerCase() + "%";
//
//            for (String col : searchableColumns) {
//                if (allowedColumns.contains(col)) {
//                    searchClauses.add("LOWER(" + col + ") LIKE ?");
//                    params.add(searchPattern);
//                }
//            }
//
//            sql.append(String.join(" OR ", searchClauses));
//            countSql.append(String.join(" OR ", searchClauses));
//            sql.append(")");
//            countSql.append(")");
//        }
//
//        // 3. Get total count
//        Long totalResults = jdbcTemplate.queryForObject(countSql.toString(), Long.class,
// params.toArray());
//        if (totalResults == null) totalResults = 0L;
//
//        // 4. Sorting & Pagination (LIMIT / OFFSET)
//        if (sortBy != null && allowedColumns.contains(sortBy)) {
//            sql.append(" ORDER BY ").append(sortBy).append("
// ").append("DESC".equalsIgnoreCase(sortDir) ? "DESC" : "ASC");
//        }
//
//        int offset = page * size;
//        sql.append(" LIMIT ? OFFSET ?");
//
//        List<Object> queryParams = new ArrayList<>(params);
//        queryParams.add(size);
//        queryParams.add(offset);
//
//        // 5. Execute query
//        List<T> results = jdbcTemplate.query(sql.toString(), rowMapper, queryParams.toArray());
//
//        int totalPages = (int) Math.ceil((double) totalResults / size);
//
//        return new PaginatedResult<>(results, totalResults, totalPages, page + 1, size);
//    }
//
//    public record PaginatedResult<T>(
//            List<T> results,
//            long totalResults,
//            int totalPages,
//            int page,
//            int limit
//    ) {}
// }
