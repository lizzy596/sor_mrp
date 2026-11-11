package org.example.sor_mrp.repository;

import org.example.sor_mrp.model.Part;
import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper;

import java.util.Map;
import java.util.Optional;

public interface PartRepository {
    Part save(Part part);
    Optional<Part> findById(Long id);
    Optional<Part> findByPartNumber(String partNumber);
    Part update(Long id, Part part);
    void deleteById(Long id);

    JdbcPaginationHelper.PaginatedResult<Part> findAllFiltered(
            Map<String, String> filters, String search, int page, int size, String sortBy, String sortDir
    );
}