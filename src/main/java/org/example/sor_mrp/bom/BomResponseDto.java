package org.example.sor_mrp.bom;

public class BomResponseDto {

    private Long id;
    private String productNumber;
    private String productDescription;

    // Default no-args constructor
    public BomResponseDto() {}

    // All-args constructor
    public BomResponseDto(Long id, String productNumber, String productDescription) {
        this.id = id;
        this.productNumber = productNumber;
        this.productDescription = productDescription;
    }

    // Factory method to map domain model to response DTO
    public static BomResponseDto fromBom(Bom bom) {
        return new BomResponseDto(
                bom.getId(),
                bom.getProductNumber(),
                bom.getProductDescription());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
