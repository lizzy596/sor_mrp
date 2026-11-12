package org.example.sor_mrp.bom;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class UpdateBomRequestDto {

  @NotNull(message = "Quantity is required")
  @DecimalMin(value = "0.0001", message = "Quantity must be greater than zero")
  private BigDecimal quantity;

  // Default constructor
  public UpdateBomRequestDto() {}

  // Parameterized constructor
  public UpdateBomRequestDto(BigDecimal quantity) {
    this.quantity = quantity;
  }

  // Getter and Setter
  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }
}
