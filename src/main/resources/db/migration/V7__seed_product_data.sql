INSERT INTO items (item_number, quantity, description, item_type) VALUES
-- Finished Goods (Complete Bikes)
('FG-ROAD-54', 45, 'Complete 54cm Endurance Road Bike - Carbon Frame', 'FINAL'),
('FG-MTB-29', 30, 'Full Suspension 29-inch Mountain Bike - Matte Black', 'FINAL'),
('FG-GRAV-56', 25, 'Gravel Adventure Bike - Shimano GRX Groupset', 'FINAL'),
('FG-COMM-50', 60, 'Urban Commuter City Bike with Integrated Fenders', 'FINAL'),

-- Sub-Assemblies
('SA-WHL-29', 120, 'Pre-laced 29-inch Tubeless Wheelset with Disc Hubs', 'COMPONENT'),
('SA-FRM-AL', 85, 'Welded 6061 Aluminum Alloy Road Bike Frame', 'COMPONENT'),
('SA-DRV-12S', 95, '12-Speed Mechanical Drivetrain Sub-Assembly', 'COMPONENT'),
('SA-HND-BAR', 150, 'Integrated Carbon Handlebar and Stem Unit', 'COMPONENT'),
('SA-FRK-CAR', 110, 'Carbon Fiber Rigid Fork Assembly with Steerer Tube', 'COMPONENT'),

-- Raw Materials
('RM-AL-TUB', 500, '6061-T6 Aluminum Extruded Mainframe Tubing (Meter)', 'BASE'),
('RM-CAR-SH', 350, 'High-Modulus Carbon Fiber Pre-Preg Sheet (Sq Meter)', 'BASE'),
('RM-STL-ROD', 800, 'Chromoly Steel Rod Stock for Small Brackets', 'BASE'),
('RM-CBL-BRA', 1200, 'Stainless Steel Inner Brake Cable (Spool/Meter)', 'BASE'),
('RM-RUB-CMP', 400, 'Synthetic Rubber Compound for Tire Tread Extrusion', 'BASE'),
('RM-BOLT-M5', 2500, 'Grade 8.8 M5 Socket Head Stainless Steel Bolts', 'BASE'),

-- Packaging
('PKG-BOX-BK', 200, 'Heavy-Duty Double-Wall Corrugated Bike Shipping Box', 'PACKAGING'),
('PKG-FOAM-KT', 300, 'Custom Molded Expanded Polyethylene Protection Kit', 'PACKAGING'),
('PKG-MAN-DOC', 500, 'Printed Owner Manual and Warranty Documentation Booklet', 'PACKAGING'),

-- MRO (Maintenance, Repair, and Operations)
('MRO-LUB-SYN', 40, 'Industrial Synthetic Assembly Grease (1kg Tub)', 'MRO'),
('MRO-CNC-BIT', 15, 'Carbide End Mill Cutting Bits for Frame CNC Machining', 'MRO')

    ON CONFLICT (item_number) DO UPDATE
                                     SET quantity = EXCLUDED.quantity,
                                     description = EXCLUDED.description,
                                     item_type = EXCLUDED.item_type,
                                     updated_at = CURRENT_TIMESTAMP;