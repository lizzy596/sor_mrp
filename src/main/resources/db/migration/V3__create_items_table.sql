CREATE TABLE items (
                       id BIGSERIAL PRIMARY KEY,
                       item_number TEXT NOT NULL,
                       quantity INT NOT NULL DEFAULT 0,
                       description TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recommended Indexes
CREATE UNIQUE INDEX idx_items_item_number ON items(item_number);
CREATE INDEX idx_items_created_at ON items(created_at DESC);