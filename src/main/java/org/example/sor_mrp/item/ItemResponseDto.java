package org.example.sor_mrp.item;

public class ItemResponseDto {

    private Long id;
    private String itemNumber;
    private ItemType itemType;
    private Integer quantity;
    private String description;

    // Default no-args constructor
    public ItemResponseDto() {}

    // All-args constructor
    public ItemResponseDto(Long id, String itemNumber, ItemType itemType, Integer quantity, String description) {
        this.id = id;
        this.itemNumber = itemNumber;
        this.itemType = itemType;
        this.quantity = quantity;
        this.description = description;
    }

    // Factory method to map domain model to response DTO
    public static ItemResponseDto fromItem(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getItemNumber(),
                item.getItemType(),
                item.getQuantity(),
                item.getDescription()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
