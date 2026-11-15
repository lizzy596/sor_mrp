package org.example.sor_mrp.bom;

import jakarta.validation.constraints.NotBlank;

public class UpdateBomRequestDto {

    @NotBlank(message = "Product number is required")
    private String productNumber;

    @NotBlank(message = "Product description is required")
    private String productDescription;

    // Default no-args constructor
    public UpdateBomRequestDto() {}

    // Parameterized constructor
    public UpdateBomRequestDto(String productNumber, String productDescription) {
        this.productNumber = productNumber;
        this.productDescription = productDescription;
    }

    // Getters and Setters
    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}