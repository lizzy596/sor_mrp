package org.example.sor_mrp.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateItemRequestDto {

    @NotBlank(message = "Item number cannot be blank")
    private String itemNumber;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private String description;

    // Default no-args constructor
    public UpdateItemRequestDto() {}

    // All-args constructor
    public UpdateItemRequestDto(String itemNumber, Integer quantity, String description) {
        this.itemNumber = itemNumber;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters and Setters
    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}