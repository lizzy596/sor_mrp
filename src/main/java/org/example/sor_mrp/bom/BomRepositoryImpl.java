package org.example.sor_mrp.bom;

import java.util.List;
import java.util.Map;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BomRepositoryImpl implements BomRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JdbcPaginationHelper paginationHelper;

    // Fixed: Match the actual columns in the bill_of_materials table
    private static final List<String> ALLOWED_COLUMNS =
            List.of("id", "product_number", "product_description", "created_at", "updated_at");

    // Fixed: Search by product number and product description
    private static final List<String> SEARCHABLE_COLUMNS = List.of("product_number", "product_description");

    public BomRepositoryImpl(
            NamedParameterJdbcTemplate jdbcTemplate, JdbcPaginationHelper paginationHelper) {
        this.jdbcTemplate = jdbcTemplate;
        this.paginationHelper = paginationHelper;
    }

    @Override
    public JdbcPaginationHelper.PaginatedResult<Bom> findAllFiltered(
            Map<String, String> filters,
            String search,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        return JdbcPaginationHelper.paginate(
                jdbcTemplate,
                "bill_of_materials", // Fixed: Match your entity's @Table name
                filters,
                search,
                SEARCHABLE_COLUMNS,
                ALLOWED_COLUMNS,
                page,
                size,
                sortBy,
                sortDir,
                (rs, rowNum) -> {
                    Bom bom = new Bom();
                    bom.setId(rs.getLong("id"));
                    bom.setProductNumber(rs.getString("product_number"));
                    bom.setProductDescription(rs.getString("product_description"));

                    if (rs.getTimestamp("created_at") != null) {
                        bom.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    if (rs.getTimestamp("updated_at") != null) {
                        bom.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                    return bom;
                });
    }
}
