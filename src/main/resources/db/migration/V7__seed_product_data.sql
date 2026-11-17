-- 1. Seed Items first (Required because bill_of_materials has a Foreign Key and Trigger checking items)
INSERT INTO items (item_number, quantity, description, item_type) VALUES
-- Finished Goods (Complete Bikes)
('FG-A', 45, 'Endurance Road Bike', 'FINAL'),
('FG-B', 30, 'Mountain Bike', 'FINAL'),
('FG-C', 25, 'Adventure Bike', 'FINAL'),
('FG-D', 60, 'Commuter City', 'FINAL'),

-- Sub-Assemblies
('SA-A', 120, 'Wheelset with Disc Hubs', 'COMPONENT'),
('SA-B', 85, 'Road Bike Frame', 'COMPONENT'),
('SA-C', 95, 'Drivetrain Sub-Assembly', 'COMPONENT'),
('SA-D', 150, 'Handlebar and Stem Unit', 'COMPONENT'),
('SA-E', 110, 'Assembly with Steerer Tube', 'COMPONENT'),

-- Raw Materials
('RM-A', 500, 'Mainframe Tubing (Meter)', 'BASE'),
('RM-B', 350, 'Pre-Preg Sheet (Sq Meter)', 'BASE'),
('RM-C', 800, 'Rod Stock for Small Brackets', 'BASE'),
('RM-D', 1200, 'Brake Cable (Spool/Meter)', 'BASE'),
('RM-E', 400, 'Synthetic Rubber Compound', 'BASE'), -- Fixed minor typo from 'Compoundn'
('RM-F', 2500, 'Socket Head Stainless Steel Bolts', 'BASE')

    ON CONFLICT (item_number) DO UPDATE
                                     SET quantity = EXCLUDED.quantity,
                                     description = EXCLUDED.description,
                                     item_type = EXCLUDED.item_type,
                                     updated_at = CURRENT_TIMESTAMP;

-- 2. Seed Bill of Materials (Only FINAL and COMPONENT items, satisfying your trigger)
INSERT INTO bill_of_materials (product_number, product_description) VALUES
-- Finished Goods (FINAL)
('FG-A', 'Endurance Road Bike'),
('FG-B', 'Mountain Bike'),
('FG-C', 'Adventure Bike'),
('FG-D', 'Commuter City'),

-- Sub-Assemblies (COMPONENT)
('SA-A', 'Wheelset with Disc Hubs'),
('SA-B', 'Road Bike Frame'),
('SA-C', 'Drivetrain Sub-Assembly'),
('SA-D', 'Handlebar and Stem Unit'),
('SA-E', 'Assembly with Steerer Tube')

    ON CONFLICT (product_number) DO UPDATE
                                        SET product_description = EXCLUDED.product_description,
                                        updated_at = CURRENT_TIMESTAMP;