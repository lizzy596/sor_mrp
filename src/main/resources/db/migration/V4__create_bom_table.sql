-- 1. Drop the old table with surrogate IDs
DROP TABLE IF EXISTS bill_of_materials CASCADE;

CREATE TABLE bill_of_materials (
                                   id BIGSERIAL PRIMARY KEY,
                                   product_number TEXT NOT NULL REFERENCES items(item_number) ON DELETE CASCADE,
                                   product_description TEXT NOT NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                CONSTRAINT uq_bom_product UNIQUE (product_number)

);

CREATE INDEX idx_bom_product ON bill_of_materials(product_number);

-- 5. Validation Function adjusted to look up by item_number
CREATE OR REPLACE FUNCTION check_item_can_have_bom()
RETURNS TRIGGER AS $$
DECLARE
product_type TEXT;
BEGIN
SELECT item_type INTO product_type
FROM items
WHERE item_number = NEW.product_number;

IF product_type IS NULL THEN
    RAISE EXCEPTION 'Item "%" does not exist', NEW.product_number;
END IF;

IF product_type NOT IN ('FINAL', 'COMPONENT') THEN
    RAISE EXCEPTION 'Item "%" of type "%" cannot have a Bill of Materials',
        NEW.product_number, product_type;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 6. Trigger on bill_of_materials
DROP TRIGGER IF EXISTS trg_validate_bom_product ON bill_of_materials;

CREATE TRIGGER trg_validate_bom_product
    BEFORE INSERT OR UPDATE ON bill_of_materials
                         FOR EACH ROW
                         EXECUTE FUNCTION check_item_can_have_bom();