-- ==============================================================================
-- REAL-TIME BUS TRACKING SYSTEM - DATABASE DDL SCHEMA (MYSQL / ANSI SQL)
-- ==============================================================================

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT fk_drivers_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 40,
    current_occupancy INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_SERVICE',
    model VARCHAR(100),
    is_simulated BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    total_distance_km DOUBLE,
    estimated_duration_minutes INT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS route_stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    stop_order INT NOT NULL,
    distance_from_start_km DOUBLE,
    scheduled_minutes_from_start INT,
    CONSTRAINT fk_rs_route FOREIGN KEY (route_id) REFERENCES routes (id) ON DELETE CASCADE,
    CONSTRAINT fk_rs_stop FOREIGN KEY (stop_id) REFERENCES stops (id) ON DELETE CASCADE,
    CONSTRAINT uq_route_stop_order UNIQUE (route_id, stop_order)
);

CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_number VARCHAR(50) NOT NULL UNIQUE,
    bus_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    driver_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    start_time DATETIME,
    end_time DATETIME,
    current_stop_id BIGINT,
    next_stop_id BIGINT,
    CONSTRAINT fk_trip_bus FOREIGN KEY (bus_id) REFERENCES buses (id),
    CONSTRAINT fk_trip_route FOREIGN KEY (route_id) REFERENCES routes (id),
    CONSTRAINT fk_trip_driver FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE SET NULL,
    CONSTRAINT fk_trip_curr_stop FOREIGN KEY (current_stop_id) REFERENCES stops (id) ON DELETE SET NULL,
    CONSTRAINT fk_trip_next_stop FOREIGN KEY (next_stop_id) REFERENCES stops (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS bus_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    trip_id BIGINT,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    speed_kmh DOUBLE NOT NULL DEFAULT 0.0,
    heading_degrees DOUBLE NOT NULL DEFAULT 0.0,
    is_simulated BOOLEAN NOT NULL DEFAULT TRUE,
    timestamp DATETIME NOT NULL,
    CONSTRAINT fk_loc_bus FOREIGN KEY (bus_id) REFERENCES buses (id) ON DELETE CASCADE,
    CONSTRAINT fk_loc_trip FOREIGN KEY (trip_id) REFERENCES trips (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_bus_loc_bus_time ON bus_locations (bus_id, timestamp DESC);
