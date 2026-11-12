package org.example.sor_mrp.bom;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateBomRequestDto {

  @NotNull(message = "Parent item ID is required")
  private Long parentItemId;

  @NotNull(message = "Child item ID is required")
  private Long childItemId;

  @NotNull(message = "Quantity is required")
  @DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
  private BigDecimal quantity;

  // Default constructor
  public CreateBomRequestDto() {}

  // Parameterized constructor
  public CreateBomRequestDto(Long parentItemId, Long childItemId, BigDecimal quantity) {
    this.parentItemId = parentItemId;
    this.childItemId = childItemId;
    this.quantity = quantity;
  }

  // Getters and Setters
  public Long getParentItemId() {
    return parentItemId;
  }

  public void setParentItemId(Long parentItemId) {
    this.parentItemId = parentItemId;
  }

  public Long getChildItemId() {
    return childItemId;
  }

  public void setChildItemId(Long childItemId) {
    this.childItemId = childItemId;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }
}
