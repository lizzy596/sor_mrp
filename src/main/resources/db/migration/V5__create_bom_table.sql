CREATE TABLE bill_of_materials (
                                   id BIGSERIAL PRIMARY KEY,
                                   parent_item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
                                   child_item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE RESTRICT,
                                   quantity NUMERIC(12, 4) NOT NULL CHECK (quantity > 0),

    -- Prevent duplicate rows for the same parent-child relationship
                                   CONSTRAINT uq_parent_child UNIQUE (parent_item_id, child_item_id),

    -- Prevent an item from being its own component
                                   CONSTRAINT chk_no_self_reference CHECK (parent_item_id <> child_item_id)
);

-- Indexes for fast tree expansion and where-used lookups
CREATE INDEX idx_bom_parent ON bill_of_materials(parent_item_id);
CREATE INDEX idx_bom_child ON bill_of_materials(child_item_id);

-- 3. Validation Function adjusted for TEXT item_type
CREATE OR REPLACE FUNCTION check_item_can_have_bom()
RETURNS TRIGGER AS $$
DECLARE
parent_type TEXT;
BEGIN
SELECT item_type INTO parent_type FROM items WHERE id = NEW.parent_item_id;

IF parent_type NOT IN ('FINISHED_GOOD', 'SUB_ASSEMBLY') THEN
        RAISE EXCEPTION 'Item type "%" cannot have a Bill of Materials', parent_type;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 4. Trigger on bill_of_materials
CREATE TRIGGER trg_validate_bom_parent
    BEFORE INSERT OR UPDATE ON bill_of_materials
                         FOR EACH ROW
                         EXECUTE FUNCTION check_item_can_have_bom();