package org.example.sor_mrp.bom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BomResponseDto {

  private Long id;
  private Long parentItemId;
  private Long childItemId;
  private BigDecimal quantity;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Default constructor
  public BomResponseDto() {}

  // Convenience constructor to map directly from your Bom domain entity
  public BomResponseDto(Bom bom) {
    this.id = bom.getId();
    this.parentItemId = bom.getParentItemId();
    this.childItemId = bom.getChildItemId();
    this.quantity = bom.getQuantity();
    this.createdAt = bom.getCreatedAt();
    this.updatedAt = bom.getUpdatedAt();
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParentItemId() {
    return parentItemId;
  }

  public void setParentItemId(Long parentItemId) {
    this.parentItemId = parentItemId;
  }

  public Long getChildItemId() {
    return childItemId;
  }

  public void setChildItemId(Long childItemId) {
    this.childItemId = childItemId;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
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
}
