-- ==============================================================================
-- REAL-TIME BUS TRACKING SYSTEM - SAMPLE TRANSIT DATA (CHENNAI / SATHYABAMA OMR)
-- ==============================================================================

-- Roles
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT IGNORE INTO roles (id, name) VALUES (2, 'ROLE_DRIVER');
INSERT IGNORE INTO roles (id, name) VALUES (3, 'ROLE_PASSENGER');

-- Stops (OMR & Chennai Transit Corridor)
INSERT IGNORE INTO stops (id, name, latitude, longitude, description) VALUES 
(1, 'Chennai Central Station', 13.0827, 80.2707, 'Main railway & metro transit hub'),
(2, 'Guindy Industrial Estate', 13.0067, 80.2030, 'Key junction near Kathipara Flyover'),
(3, 'Adyar Depot', 13.0012, 80.2565, 'Adyar bus terminus & residential hub'),
(4, 'SRP Tools (OMR Entry)', 12.9863, 80.2452, 'Entry junction to Rajiv Gandhi IT Expressway (OMR)'),
(5, 'Kandanchavadi IT Corridor', 12.9664, 80.2421, 'IT tech park hub on OMR'),
(6, 'Thoraipakkam Bus Stop', 12.9352, 80.2335, 'Thoraipakkam toll & junction'),
(7, 'Sholinganallur Junction', 12.9010, 80.2279, 'Major crossroads connecting ECR and Tambaram'),
(8, 'Sathyabama University Campus', 12.8718, 80.2206, 'Main Gate, Sathyabama Institute of Science and Technology, Jeppiaar Nagar, OMR'),
(9, 'Tambaram Bus Stand', 12.9249, 80.1000, 'South suburban rail and bus interchange'),
(10, 'Chromepet Grand Southern Trunk', 12.9516, 80.1462, 'GST road arterial stop'),
(11, 'Medavakkam Koot Road', 12.9197, 80.1873, 'Medavakkam prime cross-junction'),
(12, 'Perumbakkam Junction', 12.9056, 80.2012, 'Near Global Hospitals & residential townships');

-- Routes
INSERT IGNORE INTO routes (id, route_number, name, origin, destination, total_distance_km, estimated_duration_minutes, active) VALUES
(1, 'ROUTE-101', 'Central Station to Sathyabama Campus (OMR Express)', 'Chennai Central Station', 'Sathyabama University Campus', 28.5, 65, true),
(2, 'ROUTE-102', 'Tambaram to Sathyabama Campus (Medavakkam Feeder)', 'Tambaram Bus Stand', 'Sathyabama University Campus', 18.2, 45, true);

-- Route Stops for ROUTE-101
INSERT IGNORE INTO route_stops (id, route_id, stop_id, stop_order, distance_from_start_km, scheduled_minutes_from_start) VALUES
(1, 1, 1, 1, 0.0, 0),
(2, 1, 2, 2, 11.0, 22),
(3, 1, 4, 3, 15.5, 32),
(4, 1, 5, 4, 18.2, 39),
(5, 1, 6, 5, 21.8, 48),
(6, 1, 7, 6, 25.4, 57),
(7, 1, 8, 7, 28.5, 65);

-- Route Stops for ROUTE-102
INSERT IGNORE INTO route_stops (id, route_id, stop_id, stop_order, distance_from_start_km, scheduled_minutes_from_start) VALUES
(8, 2, 9, 1, 0.0, 0),
(9, 2, 10, 2, 4.2, 10),
(10, 2, 11, 3, 9.8, 24),
(11, 2, 12, 4, 13.1, 32),
(12, 2, 7, 5, 15.6, 38),
(13, 2, 8, 6, 18.2, 45);

-- Buses
INSERT IGNORE INTO buses (id, bus_number, registration_number, capacity, current_occupancy, status, model, is_simulated) VALUES
(1, 'BUS-101', 'TN-01-AN-2024', 45, 28, 'IN_SERVICE', 'Tata Starbus Ultra AC', true),
(2, 'BUS-102', 'TN-07-BL-4590', 50, 36, 'IN_SERVICE', 'Ashok Leyland Lynx Express', true),
(3, 'BUS-103', 'TN-14-CZ-8821', 40, 15, 'IN_SERVICE', 'Eicher Starline Transit', true),
(4, 'BUS-104', 'TN-22-EX-1104', 45, 0, 'MAINTENANCE', 'BharatBenz Tourliner', false);

-- Drivers
INSERT IGNORE INTO drivers (id, license_number, full_name, phone, status) VALUES
(1, 'DL-TN-01-2015-00123', 'Ramesh Kumar', '+91 98765 43210', 'ON_TRIP'),
(2, 'DL-TN-07-2018-00456', 'Suresh Balaji', '+91 98765 43211', 'ON_TRIP'),
(3, 'DL-TN-14-2020-00789', 'Murugan Sundaram', '+91 98765 43212', 'AVAILABLE');

-- Initial Trips
INSERT IGNORE INTO trips (id, trip_number, bus_id, route_id, driver_id, status, start_time, current_stop_id, next_stop_id) VALUES
(1, 'TRIP-101-AM', 1, 1, 1, 'IN_PROGRESS', NOW(), 4, 5),
(2, 'TRIP-102-AM', 2, 2, 2, 'IN_PROGRESS', NOW(), 11, 12);

-- Initial Bus Locations
INSERT IGNORE INTO bus_locations (id, bus_id, trip_id, latitude, longitude, speed_kmh, heading_degrees, is_simulated, timestamp) VALUES
(1, 1, 1, 12.9863, 80.2452, 38.5, 182.0, true, NOW()),
(2, 2, 2, 12.9197, 80.1873, 42.0, 115.0, true, NOW());
