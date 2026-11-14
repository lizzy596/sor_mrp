
-- 3. Create the Bill of Materials table using item_number (TEXT)
CREATE TABLE bill_of_materials_item (
                                   id BIGSERIAL PRIMARY KEY,
                                   parent_item_number TEXT NOT NULL REFERENCES items(item_number) ON DELETE CASCADE,
                                   child_item_number TEXT NOT NULL REFERENCES items(item_number) ON DELETE RESTRICT,
                                   quantity NUMERIC(12, 4) NOT NULL CHECK (quantity > 0),

    -- Prevent duplicate rows for the same product-child relationship
                                   CONSTRAINT uq_parent_child UNIQUE (parent_item_number, child_item_number),

    -- Prevent an item from being its own component
                                   CONSTRAINT chk_no_self_reference CHECK (parent_item_number <> child_item_number)
);

-- 4. Indexes for fast tree expansion and where-used lookups
CREATE INDEX idx_bom_parent_item_number ON bill_of_materials_item(parent_item_number);
CREATE INDEX idx_bom_child ON bill_of_materials_item(child_item_number);

-- 5. Validation Function adjusted to look up by item_number
CREATE OR REPLACE FUNCTION check_item_can_have_bom_item()
RETURNS TRIGGER AS $$
DECLARE
product_type TEXT;
BEGIN
SELECT item_type INTO product_type
FROM items
WHERE item_number = NEW.parent_item_number;

IF product_type NOT IN ('FINAL', 'COMPONENT') THEN
        RAISE EXCEPTION 'Item "%" of type "%" cannot have a Bill of Materials', NEW.parent_item_number, product_type;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6. Trigger on bill_of_materials_item
DROP TRIGGER IF EXISTS trg_validate_bom_product ON bill_of_materials_item;

CREATE TRIGGER trg_validate_bom_product
    BEFORE INSERT OR UPDATE ON bill_of_materials_item
                         FOR EACH ROW
                         EXECUTE FUNCTION check_item_can_have_bom_item();