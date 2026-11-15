package org.example.sor_mrp.bom.bom_item;

import java.math.BigDecimal;

public class BomItemResponseDto {
    private Long id;
    private String parentItemNumber;
    private String childItemNumber;
    private BigDecimal quantity;


    public BomItemResponseDto() {}

    // All-args constructor
    public BomItemResponseDto(Long id, String parentItemNumber, String childItemNumber, BigDecimal quantity) {
        this.id = id;
        this.parentItemNumber = parentItemNumber;
        this.childItemNumber = childItemNumber;
        this.quantity = quantity;
    }

    // Factory method to map domain model to response DTO
    public static BomItemResponseDto fromBomItem(BomItem bomitem) {
        return new BomItemResponseDto(
                bomitem.getId(),
                bomitem.getParentItemNumber(),
                bomitem.getChildItemNumber(),
                bomitem.getQuantity());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentItemNumber() {
        return parentItemNumber;
    }

   public void setParentItemNumber() {
        this.parentItemNumber = parentItemNumber;
   }

   public String getChildItemNumber() {
        return childItemNumber;
   }

   public void setChildItemNumber() {
        this.childItemNumber = childItemNumber;
   }

   public BigDecimal getQuantity() {
        return quantity;
   }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
