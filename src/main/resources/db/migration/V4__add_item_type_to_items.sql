
ALTER TABLE items
    ADD COLUMN item_type TEXT NOT NULL DEFAULT 'RAW_MATERIAL'
        CHECK (item_type IN ('RAW_MATERIAL', 'SUB_ASSEMBLY', 'FINISHED_GOOD', 'PACKAGING', 'MRO'));

CREATE INDEX idx_items_item_type ON items(item_type);