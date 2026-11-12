package org.example.sor_mrp.bom;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper.PaginatedResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BomRepository {

    Bom save(Bom bom);

    Optional<Bom> findById(Long id);

    List<Bom> findByParentItemId(Long parentItemId);

    List<Bom> findByChildItemId(Long childItemId);

    void deleteById(Long id);

    boolean existsByParentAndChild(Long parentItemId, Long childItemId);


    PaginatedResult<BomDetail> findDetailedPaginated(
            int page, int size, String sortBy, String sortDir, String search, Map<String, String> filters);
}
