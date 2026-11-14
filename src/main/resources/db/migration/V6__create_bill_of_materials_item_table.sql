-- 1. Drop the old table with surrogate IDs
DROP TABLE IF EXISTS bill_of_materials CASCADE;

-- 2. Ensure items.item_number has a unique constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_items_item_number'
    ) THEN
ALTER TABLE items ADD CONSTRAINT uq_items_item_number UNIQUE (item_number);
END IF;
END $$;

-- 3. Create the Bill of Materials table using item_number (TEXT)
CREATE TABLE bill_of_materials (
                                   id BIGSERIAL PRIMARY KEY,
                                   parent_item_number TEXT NOT NULL REFERENCES items(item_number) ON DELETE CASCADE,
                                   child_item_number TEXT NOT NULL REFERENCES items(item_number) ON DELETE RESTRICT,
                                   quantity NUMERIC(12, 4) NOT NULL CHECK (quantity > 0),

    -- Prevent duplicate rows for the same parent-child relationship
                                   CONSTRAINT uq_parent_child UNIQUE (parent_item_number, child_item_number),

    -- Prevent an item from being its own component
                                   CONSTRAINT chk_no_self_reference CHECK (parent_item_number <> child_item_number)
);

-- 4. Indexes for fast tree expansion and where-used lookups
CREATE INDEX idx_bom_parent ON bill_of_materials(parent_item_number);
CREATE INDEX idx_bom_child ON bill_of_materials(child_item_number);

-- 5. Validation Function adjusted to look up by item_number
CREATE OR REPLACE FUNCTION check_item_can_have_bom()
RETURNS TRIGGER AS $$
DECLARE
parent_type TEXT;
BEGIN
SELECT item_type INTO parent_type
FROM items
WHERE item_number = NEW.parent_item_number;

IF parent_type NOT IN ('FINISHED_GOOD', 'SUB_ASSEMBLY') THEN
        RAISE EXCEPTION 'Item "%" of type "%" cannot have a Bill of Materials', NEW.parent_item_number, parent_type;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6. Trigger on bill_of_materials
DROP TRIGGER IF EXISTS trg_validate_bom_parent ON bill_of_materials;

CREATE TRIGGER trg_validate_bom_parent
    BEFORE INSERT OR UPDATE ON bill_of_materials
                         FOR EACH ROW
                         EXECUTE FUNCTION check_item_can_have_bom();