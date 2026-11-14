-- Drop the old table if it exists (use CASCADE if foreign keys reference it)
DROP TABLE IF EXISTS items CASCADE;

-- Create the unified items table
CREATE TABLE items (
                       id BIGSERIAL PRIMARY KEY,
                       item_number TEXT NOT NULL,
                       quantity INT NOT NULL DEFAULT 0,
                       description TEXT,
                       item_type TEXT NOT NULL DEFAULT 'BASE'
                           CHECK (item_type IN ('BASE', 'COMPONENT', 'FINAL', 'PACKAGING', 'MRO')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recommended Indexes
CREATE UNIQUE INDEX idx_items_item_number ON items(item_number);
CREATE INDEX idx_items_item_type ON items(item_type);
CREATE INDEX idx_items_created_at ON items(created_at DESC);