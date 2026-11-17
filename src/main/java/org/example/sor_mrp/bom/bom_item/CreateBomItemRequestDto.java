package org.example.sor_mrp.bom.bom_item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public class CreateBomItemRequestDto {

  @NotBlank(message = "Parent item number cannot be blank")
  private String parentItemNumber;

  @NotBlank(message = "Child item number cannot be blank")
  private String childItemNumber;

  @NotNull(message = "Quantity is required")
  @Positive(message = "Quantity must be greater than zero")
  private BigDecimal quantity;

  // Default constructor required for JSON deserialization (Jackson)
  public CreateBomItemRequestDto() {}

  public CreateBomItemRequestDto(
      String parentItemNumber, String childItemNumber, BigDecimal quantity) {
    this.parentItemNumber = parentItemNumber;
    this.childItemNumber = childItemNumber;
    this.quantity = quantity;
  }

  // Getters and Setters
  public String getParentItemNumber() {
    return parentItemNumber;
  }

  public void setParentItemNumber(String parentItemNumber) {
    this.parentItemNumber = parentItemNumber;
  }

  public String getChildItemNumber() {
    return childItemNumber;
  }

  public void setChildItemNumber(String childItemNumber) {
    this.childItemNumber = childItemNumber;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreateBomItemRequestDto that = (CreateBomItemRequestDto) o;
    return Objects.equals(parentItemNumber, that.parentItemNumber)
        && Objects.equals(childItemNumber, that.childItemNumber)
        && Objects.equals(quantity, that.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentItemNumber, childItemNumber, quantity);
  }

  @Override
  public String toString() {
    return "CreateBomItemRequestDto{"
        + "parentItemNumber='"
        + parentItemNumber
        + '\''
        + ", childItemNumber='"
        + childItemNumber
        + '\''
        + ", quantity="
        + quantity
        + '}';
  }
}
