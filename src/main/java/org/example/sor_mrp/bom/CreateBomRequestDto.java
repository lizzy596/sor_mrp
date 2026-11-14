package org.example.sor_mrp.bom;

import jakarta.validation.constraints.NotBlank;

public class CreateBomRequestDto {

  @NotBlank(message = "Product number is required")
  private String productNumber;

  @NotBlank(message = "Product description is required")
  private String productDescription;

  // Default constructor
  public CreateBomRequestDto() {}

  // Parameterized constructor
  public CreateBomRequestDto(String productNumber, String productDescription) {
    this.productNumber = productNumber;
    this.productDescription = productDescription;
  }

  // Getter and Setter for productNumber
  public String getProductNumber() {
    return productNumber;
  }

  public void setProductNumber(String productNumber) {
    this.productNumber = productNumber;
  }

  // Getter and Setter for productDescription
  public String getProductDescription() {
    return productDescription;
  }

  public void setProductDescription(String productDescription) {
    this.productDescription = productDescription;
  }
}
