package org.example.sor_mrp.bom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BomDetail(
        Long id,
        Long parentItemId,
        String parentItemNumber,
        String parentDescription,
        Long childItemId,
        String childItemNumber,
        String childDescription,
        BigDecimal quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
