package org.example.sor_mrp.bom;

import org.example.sor_mrp.shared.configuration.utils.JdbcPaginationHelper.PaginatedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BomService {

    private final BomRepository bomRepository;

    public BomService(BomRepository bomRepository) {
        this.bomRepository = bomRepository;
    }

    @Transactional
    public Bom createBom(CreateBomRequestDto requestDto) {
        Long parentId = requestDto.getParentItemId();
        Long childId = requestDto.getChildItemId();

        // 1. Prevent self-reference
        if (parentId.equals(childId)) {
            throw new IllegalArgumentException("An item cannot be a component of itself.");
        }

        // 2. Prevent duplicate parent-child links
        if (bomRepository.existsByParentAndChild(parentId, childId)) {
            throw new IllegalArgumentException("This parent-child BOM relationship already exists.");
        }

        // 3. Prevent circular dependencies (Cycle Detection)
        if (wouldCreateCycle(parentId, childId)) {
            throw new IllegalArgumentException("Circular dependency detected: Cannot add an ancestor as a component.");
        }

        Bom bom = new Bom(parentId, childId, requestDto.getQuantity());
        return bomRepository.save(bom);
    }

    @Transactional
    public Bom updateBom(Long id, UpdateBomRequestDto requestDto) {
        Bom existingBom = bomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill of Materials record with ID " + id + " not found."));

        existingBom.setQuantity(requestDto.getQuantity());
        return bomRepository.save(existingBom);
    }

    public Bom findById(Long id) {
        return bomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill of Materials record with ID " + id + " not found."));
    }

    public List<Bom> findComponentsByParentId(Long parentItemId) {
        return bomRepository.findByParentItemId(parentItemId);
    }

    public List<Bom> findWhereUsedByChildId(Long childItemId) {
        return bomRepository.findByChildItemId(childItemId);
    }

    @Transactional
    public void deleteBom(Long id) {
        findById(id);
        bomRepository.deleteById(id);
    }

    public PaginatedResult<BomDetail> getPaginatedBoms(
            int page, int size, String sortBy, String sortDir, String search, Map<String, String> filters) {
        return bomRepository.findDetailedPaginated(page, size, sortBy, sortDir, search, filters);
    }

    private boolean wouldCreateCycle(Long parentId, Long childId) {
        List<Bom> childComponents = bomRepository.findByParentItemId(childId);
        if (childComponents.isEmpty()) {
            return false;
        }

        for (Bom component : childComponents) {
            if (component.getChildItemId().equals(parentId)) {
                return true;
            }
            if (wouldCreateCycle(parentId, component.getChildItemId())) {
                return true;
            }
        }
        return false;
    }
}