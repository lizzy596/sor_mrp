package org.example.sor_mrp.bom.bom_item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BatchUpdateBomItemRequestDto {
  @NotBlank(message = "Parent item number is required")
  private String parentItemNumber;

  @NotEmpty(message = "Item list cannot be empty")
  @Valid // Ensures validation runs on every item inside the list
  private List<BatchUpdateBomItemRequestDto.ItemDetail> items;

  // Nested class for individual child items
  public static class ItemDetail {
    @NotBlank(message = "Child item number is required")
    private String childItemNumber;

    @NotNull(message = "Quantity is required")
    private java.math.BigDecimal quantity;

    // Getters and Setters...
    public String getChildItemNumber() {
      return childItemNumber;
    }

    public void setChildItemNumber(String childItemNumber) {
      this.childItemNumber = childItemNumber;
    }

    public java.math.BigDecimal getQuantity() {
      return quantity;
    }

    public void setQuantity(java.math.BigDecimal quantity) {
      this.quantity = quantity;
    }
  }

  // Getters and Setters for Batch DTO
  public String getParentItemNumber() {
    return parentItemNumber;
  }

  public void setParentItemNumber(String parentItemNumber) {
    this.parentItemNumber = parentItemNumber;
  }

  public List<BatchUpdateBomItemRequestDto.ItemDetail> getItems() {
    return items;
  }

  public void setItems(List<BatchUpdateBomItemRequestDto.ItemDetail> items) {
    this.items = items;
  }
}
