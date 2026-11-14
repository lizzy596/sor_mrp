package org.example.sor_mrp.item;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "items") // Adjust if your table name is different
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "item_number", nullable = false, unique = true)
  private String itemNumber;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "description")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_type", nullable = false)
  private ItemType itemType;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  // Default no-args constructor (required by frameworks like JPA and Jackson)
  public Item() {}

  // All-args constructor for convenient instantiation
  public Item(
      Long id,
      String itemNumber,
      Integer quantity,
      String description,
      ItemType itemType,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.itemNumber = itemNumber;
    this.quantity = quantity;
    this.description = description;
    this.itemType = itemType;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
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

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  // --- Nice-to-Have Methods ---

  @Override
  public String toString() {
    return "Item{"
        + "id="
        + id
        + ", itemNumber='"
        + itemNumber
        + '\''
        + ", quantity="
        + quantity
        + ", description='"
        + description
        + '\''
        + ", itemType="
        + itemType
        + '}';
  }
}
