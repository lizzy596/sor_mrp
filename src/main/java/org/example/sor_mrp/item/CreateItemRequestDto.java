package org.example.sor_mrp.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateItemRequestDto {

    @NotBlank(message = "Item number cannot be blank")
    private String itemNumber;

    @NotNull(message = "Item type is required")
    private ItemType itemType;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private String description; // Optional field

    // Default no-args constructor (required for JSON deserialization)
    public CreateItemRequestDto() {}

    // All-args constructor
    public CreateItemRequestDto(String itemNumber, ItemType itemType, Integer quantity, String description) {
        this.itemNumber = itemNumber;
        this.itemType = itemType;
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

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
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