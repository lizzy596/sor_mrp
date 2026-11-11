package org.example.sor_mrp.item;

public class ItemResponseDto {

    private Long id;
    private String itemNumber;
    private Integer quantity;
    private String description;

    // Default no-args constructor
    public ItemResponseDto() {}

    // All-args constructor
    public ItemResponseDto(Long id, String itemNumber, Integer quantity, String description) {
        this.id = id;
        this.itemNumber = itemNumber;
        this.quantity = quantity;
        this.description = description;
    }

    // --- ADD THIS METHOD ---
    public static ItemResponseDto fromItem(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getItemNumber(),
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
