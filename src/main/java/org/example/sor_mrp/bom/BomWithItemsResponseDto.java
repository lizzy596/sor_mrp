package org.example.sor_mrp.bom;

import org.example.sor_mrp.bom.bom_item.BomItem;
import java.util.List;

public class BomWithItemsResponseDto {
    private Bom bom;
    private List<BomItem> items;

    public BomWithItemsResponseDto(Bom bom, List<BomItem> items) {
        this.bom = bom;
        this.items = items;
    }

    // Getters and Setters
    public Bom getBom() { return bom; }
    public void setBom(Bom bom) { this.bom = bom; }

    public List<BomItem> getItems() { return items; }
    public void setItems(List<BomItem> items) { this.items = items; }
}
