package org.example.sor_mrp.repository;

import org.example.sor_mrp.model.Part;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcPartRepository implements PartRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPartRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Part> partRowMapper = (rs, rowNum) -> new Part(
            rs.getLong("id"),
            rs.getString("part_number"),
            rs.getInt("quantity"),
            rs.getString("description")
    );

    @Override
    public Part save(Part part) {
        String sql = "INSERT INTO parts (part_number, quantity, description) VALUES (:partNumber, :quantity, :description) RETURNING id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("partNumber", part.getPartNumber())
                .addValue("quantity", part.getQuantity())
                .addValue("description", part.getDescription());

        Long id = jdbcTemplate.queryForObject(sql, params, Long.class);
        part.setId(id);
        return part;
    }

    @Override
    public Optional<Part> findById(Long id) {
        String sql = "SELECT id, part_number, quantity, description FROM parts WHERE id = :id";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource("id", id);
            Part part = jdbcTemplate.queryForObject(sql, params, partRowMapper);
            return Optional.ofNullable(part);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Part> findByPartNumber(String partNumber) {
        String sql = "SELECT id, part_number, quantity, description FROM parts WHERE part_number = :partNumber";
        try {
            MapSqlParameterSource params = new MapSqlParameterSource("partNumber", partNumber);
            Part part = jdbcTemplate.queryForObject(sql, params, partRowMapper);
            return Optional.ofNullable(part);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Part update(Long id, Part part) {
        String sql = "UPDATE parts SET part_number = :partNumber, quantity = :quantity, description = :description WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("partNumber", part.getPartNumber())
                .addValue("quantity", part.getQuantity())
                .addValue("description", part.getDescription());

        jdbcTemplate.update(sql, params);
        part.setId(id);
        return part;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM parts WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public JdbcPaginationHelper.PaginatedResult<Part> findAllFiltered(
            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
    ) {
        // Whitelisted columns mapped directly to your parts table structure
        List<String> allowedColumns = List.of("id", "part_number", "quantity", "description");
        List<String> searchableColumns = List.of("part_number", "description");

        return JdbcPaginationHelper.paginate(
                jdbcTemplate,
                "parts",      // Table name
                filters,
                search,
                searchableColumns,
                allowedColumns,
                page,
                size,
                sortBy,
                sortDir,
                partRowMapper
        );
    }
}
