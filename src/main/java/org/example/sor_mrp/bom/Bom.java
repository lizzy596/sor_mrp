package org.example.sor_mrp.bom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bom {
  private Long id;
  private Long parentItemId;
  private Long childItemId;
  private BigDecimal quantity;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Default constructor (required by Spring JDBC / RowMappers)
  public Bom() {}

  // Convenience constructor for inserts
  public Bom(Long parentItemId, Long childItemId, BigDecimal quantity) {
    this.parentItemId = parentItemId;
    this.childItemId = childItemId;
    this.quantity = quantity;
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
