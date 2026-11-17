package org.example.sor_mrp.bom.bom_item;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(
    name = "bill_of_materials_item",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uq_parent_child",
          columnNames = {"parent_item_number", "child_item_number"})
    })
public class BomItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "parent_item_number", nullable = false, length = 100)
  private String parentItemNumber;

  @Column(name = "child_item_number", nullable = false, length = 100)
  private String childItemNumber;

  @Column(name = "quantity", precision = 12, scale = 4, nullable = false)
  private BigDecimal quantity;

  // Default constructor required by JPA
  public BomItem() {}

  public BomItem(String parentItemNumber, String childItemNumber, BigDecimal quantity) {
    this.parentItemNumber = parentItemNumber;
    this.childItemNumber = childItemNumber;
    this.quantity = quantity;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getParentItemNumber() {
    return parentItemNumber;
  }

  public void setParentItemNumber(String parentItemNumber) {
    this.parentItemNumber = parentItemNumber;
  }

  public String getChildItemNumber() {
    return childItemNumber;
  }

  public void setChildItemNumber(String childItemNumber) {
    this.childItemNumber = childItemNumber;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  // equals() and hashCode() based on natural business key (parent + child relationship)
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BomItem bomItem = (BomItem) o;
    return Objects.equals(parentItemNumber, bomItem.parentItemNumber)
        && Objects.equals(childItemNumber, bomItem.childItemNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentItemNumber, childItemNumber);
  }

  @Override
  public String toString() {
    return "BomItem{"
        + "id="
        + id
        + ", parentItemNumber='"
        + parentItemNumber
        + '\''
        + ", childItemNumber='"
        + childItemNumber
        + '\''
        + ", quantity="
        + quantity
        + '}';
  }
}
