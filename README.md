# Real-Time Bus Tracking System (Java Only)

> **Academic Project — Course Level Project-Based Learning**  
> **Institution:** Sathyabama Institute of Science and Technology, Chennai  
> **Department:** Department of Computer Science and Engineering  
> **Project Team:** Naseer Ahmed (Lead), Naji (Backend), Uttam Choudhary (UI/UX), Abunav K Menon (Database & API), Tanmay Marolia (Testing & Integration), Santosh (Documentation & QA)

---

## 1. Project Overview & Problem Statement

Commuters and university students regularly experience unpredictable transit waiting times, severe schedule uncertainties, and overcrowded buses due to the absence of live telemetry. Manual fleet dispatching cannot dynamically adjust for traffic congestion, signal delays, or unexpected breakdowns.

The **Real-Time Bus Tracking System** is an end-to-end, enterprise-grade, **100% Java-based transit management application**. It delivers:
- **Sub-2-second live GPS bus tracking** on an interactive OpenStreetMap canvas.
- **Dynamic arrival countdowns (ETA)** calculated using Java spherical geodetic algorithms (Great-Circle Haversine formula).
- **Seat occupancy & crowd level alerts** ("Low Crowding", "Moderate", "Crowded").
- **Multithreaded Java GPS simulator** generating realistic vehicle trajectories between real geographic route waypoints.
- **Role-based security** with dedicated modules for Transit Administrators, Fleet Drivers, and Commuters/Students.

---

## 2. Mandatory Technology Stack

This system is built exclusively using Java technologies:

| Layer | Technology | Specification / Role |
|---|---|---|
| **Programming Language** | **Java** | Core business logic, thread management, mathematical geodesy |
| **Java Version** | **JDK 17 LTS / JDK 21 / JDK 24** | Modern LTS support with bytecode compilation to Java 17 |
| **Application Framework** | **Spring Boot 3.3.5** | Web MVC, dependency injection, application lifecycle |
| **Persistence / ORM** | **Spring Data JPA & Hibernate 6** | Relational data mapping, connection pooling with HikariCP |
| **Production Database** | **MySQL 8.0** | Relational database schema with foreign keys and composite indexes |
| **Standalone / Demo Database**| **H2 In-Memory (MySQL Mode)** | Zero-friction local demonstration without external database setup |
| **Web Presentation** | **Thymeleaf + Semantic HTML5 + CSS3** | Server-side rendered templates; no separate JS frontend |
| **Real-Time Updates** | **Spring WebSocket + STOMP** | Pub/Sub location broadcast (`/topic/bus-locations`) |
| **Map Engine** | **Leaflet.js & OpenStreetMap** | Map tile visualization embedded directly in Thymeleaf |
| **Security & Authentication** | **Spring Security 6 (BCrypt)** | Role-based authorization (`ROLE_ADMIN`, `ROLE_DRIVER`, `ROLE_PASSENGER`) |
| **Build & Dependency Tool** | **Apache Maven 3.9+** | Build management and artifact packaging |
| **Automated Testing** | **JUnit 5 & Spring Boot Test** | Unit and integration test suite (28 automated tests) |

---

## 3. System Architecture & Project Organization

The codebase adheres strictly to layered Java software engineering principles:

```
realtime-bus-tracking-system/
├── pom.xml                                  # Maven dependencies & build plugins
├── .env.example                             # Configuration environment variables template
├── .gitignore                               # Git ignore configuration
├── README.md                                # Project documentation & demonstration guide
├── src/
│   ├── main/
│   │   ├── java/com/bustracking/
│   │   │   ├── BusTrackingApplication.java  # Spring Boot application entry point
│   │   │   ├── config/                      # SecurityConfig, WebSocketConfig, DatabaseInitializer
│   │   │   ├── controller/                  # PassengerController, AdminController, DriverController, SimulationApiController
│   │   │   ├── dto/                         # BusDto, BusLocationDto, EtaResponseDto, RouteDto, TripDto
│   │   │   ├── entity/                      # User, Role, Bus, Driver, Route, Stop, RouteStop, Trip, BusLocation
│   │   │   ├── exception/                   # GlobalExceptionHandler, ResourceNotFoundException
│   │   │   ├── gps/                         # HaversineDistanceCalculator, Coordinate, GpsSimulatorService
│   │   │   ├── repository/                  # Spring Data JPA repositories (9 interfaces)
│   │   │   ├── security/                    # CustomUserDetails, CustomUserDetailsService
│   │   │   └── service/                     # BusService, RouteService, TripService, DriverService, BusLocationService, EtaCalculationService
│   │   └── resources/
│   │       ├── application.properties       # Base application configuration
│   │       ├── application-mysql.properties # MySQL database profile
│   │       ├── application-h2.properties    # Standalone H2 in-memory profile
│   │       ├── schema.sql                   # MySQL ANSI SQL DDL schema
│   │       ├── data.sql                     # Seed data for OMR / Chennai transit corridor
│   │       ├── static/
│   │       │   ├── css/style.css            # Custom CSS styling (responsive, cards, badges)
│   │       │   └── js/tracking-map.js       # Minimal Leaflet map tile rendering script
│   │       └── templates/                   # Thymeleaf HTML views
│   │           ├── fragments/               # navbar.html, layout.html
│   │           ├── passenger/               # home.html, track.html, routes.html, route-detail.html, search.html
│   │           ├── admin/                   # dashboard.html, buses.html, routes.html, stops.html, drivers.html, trips.html, simulation.html
│   │           ├── driver/                  # dashboard.html
│   │           ├── auth/                    # login.html, register.html
│   │           └── error/                   # 403.html, 404.html, 500.html
│   └── test/
│       ├── java/com/bustracking/            # 28 Automated JUnit 5 tests
│       └── resources/
│           ├── application-test.properties  # Isolated test profile configuration
│           └── mockito-extensions/          # Mockito bytecode plugin configuration
```

---

## 4. Key Functional Modules

### A. Commuter & Student Module
- **Live Interactive Map:** Visualizes buses moving in real time with animated transit pins and bearing orientation.
- **Route Catalog & Stops Timeline:** Detailed step-by-step stop listings, cumulative distance in kilometers, and scheduled minutes.
- **Dynamic ETA Countdown:** Arrival predictions computed via the Haversine formula based on vehicle velocity.
- **Seat Availability & Crowding:** Visual color-coded capacity indicators ("Low Crowding", "Moderate", "Crowded").
- **Transit Search:** Search routes, landmarks, and bus numbers.

### B. Fleet Administration Module
- **Operations Dashboard:** Fleet KPIs, total buses, active routes, available drivers, and running trips.
- **Bus Fleet CRUD:** Add, update, view, and delete buses, set seating capacities, and toggle GPS simulation mode.
- **Route & Stop Management:** Create routes, define stop waypoints with latitude/longitude coordinates, and reorder sequences.
- **Driver Management:** Maintain driver profiles, commercial license details, and availability status.
- **Trip Scheduler:** Dispatch trips, assign drivers and buses to specific routes, and mark trips `IN_PROGRESS` or `COMPLETED`.
- **GPS Simulation Command Center:** Real-time engine status, velocity presets (1.0x, 2.0x, 3.5x, 5.0x), and Start/Stop controls.

### C. Driver Onboard Module
- **Driver Authentication:** Secure driver sign-in.
- **Assigned Trip Console:** Displays current route, bus registration, passenger load, and next scheduled stop.
- **Trip Lifecycle Controls:** Start trip and mark trip finished.
- **GPS Beacon Interface:** Transmit manual GPS coordinates or browser geolocation sensor pings directly to the backend.

---

## 5. Mathematical Geodesy & ETA Computation in Java

All tracking math is implemented in pure Java (`com.bustracking.gps.HaversineDistanceCalculator`):

1. **Haversine Distance Formula:**
   $$\Delta\sigma = 2 \arcsin \left( \sqrt{\sin^2\left(\frac{\Delta\phi}{2}\right) + \cos(\phi_1)\cos(\phi_2)\sin^2\left(\frac{\Delta\lambda}{2}\right)} \right)$$
   $$\text{Distance (km)} = R \cdot \Delta\sigma \quad (\text{where } R = 6371.0 \text{ km})$$

2. **Azimuth / Compass Bearing:**
   $$\theta = \text{atan2}\left(\sin(\Delta\lambda)\cos(\phi_2), \cos(\phi_1)\sin(\phi_2) - \sin(\phi_1)\cos(\phi_2)\cos(\Delta\lambda)\right)$$

3. **Waypoint Linear Interpolation:**
   Smooth movement between successive GPS route stops based on elapsed simulation time and vehicle velocity.

4. **Estimated Time of Arrival (ETA):**
   $$\text{ETA (minutes)} = \frac{\text{Remaining Distance (km)}}{\text{Effective Speed (km/h)}} \times 60$$

---

## 6. Prerequisites

- **Java Development Kit (JDK):** JDK 17, 21, or 24.
- **Build Tool:** Apache Maven 3.9+ (or use the portable maven installed in `C:\Users\yashc\tools\apache-maven-3.9.9\bin`).
- **Database (Optional):** MySQL Server 8.0+ (the system also includes an instant zero-configuration H2 in-memory mode).

---

## 7. Database Setup & Configuration

### Option A: Using MySQL 8.0 (Recommended for Production)

1. Ensure MySQL Server is running on port 3306.
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS bus_tracking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Set your environment variables (or copy `.env.example` to `.env`):
   ```bash
   export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/bus_tracking_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
   export SPRING_DATASOURCE_USERNAME="root"
   export SPRING_DATASOURCE_PASSWORD="your_mysql_password"
   export SPRING_PROFILES_ACTIVE="mysql"
   ```
4. Run the application; Hibernate and `DatabaseInitializer` will automatically create the tables and seed default users, buses, routes, and trips.

### Option B: Zero-Config Standalone Demo (H2 Mode)

No database installation required! Run the application with the `h2` profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```
or run the packaged JAR:
```bash
java -jar target/realtime-bus-tracking-system-1.0.0.jar --spring.profiles.active=h2
```

---

## 8. Build & Execution Commands

### 1. Build and Run Automated Tests
```bash
mvn clean test
```
*Executes all 28 automated JUnit 5 tests covering security, controllers, services, mathematical formulas, and simulation.*

### 2. Package Executable JAR
```bash
mvn clean package -DskipTests
```
*Creates `target/realtime-bus-tracking-system-1.0.0.jar`.*

### 3. Run the Application
```bash
java -jar target/realtime-bus-tracking-system-1.0.0.jar --spring.profiles.active=h2
```
*Access the application at:* **http://localhost:8080**

---

## 9. Demonstration Walkthrough & Default Accounts

The system automatically initializes default accounts at startup:

| Role | Username | Password | Default Redirect | Purpose |
|---|---|---|---|---|
| **Administrator** | `admin` | `admin123` | `/admin/dashboard` | Fleet management, bus/route CRUD, simulator control |
| **Driver** | `driver1` | `driver123` | `/driver/dashboard` | Assigned trip execution, GPS beacon transmission |
| **Passenger / Student**| `student1` | `student123` | `/track` | Live tracking, routes catalog, ETA countdowns |

### Step-by-Step Academic Demo:

1. **Live Commuter Map:**
   - Open browser at `http://localhost:8080/track`.
   - Watch buses `BUS-101` and `BUS-102` move in real time along the OMR / Sathyabama transit corridor.
   - Click on any bus to open its live telemetry popup displaying velocity, next stop, arrival countdown, and crowd capacity.
2. **Explore Routes:**
   - Navigate to `http://localhost:8080/routes`.
   - Click **View Stop Schedule** on `ROUTE-101 (Central Station to Sathyabama Campus)` to inspect the 7-stop timeline.
3. **Admin Fleet Management:**
   - Log in at `http://localhost:8080/login` with `admin` / `admin123`.
   - View the **Operations Command Dashboard** showing active fleet metrics.
   - Go to **Bus Management** (`/admin/buses`) to add a new bus or edit seating capacity.
   - Go to **Trip Schedules** (`/admin/trips`) to dispatch a new trip.
4. **GPS Simulator Velocity Adjustments:**
   - Open **GPS Simulator Control** (`http://localhost:8080/admin/simulation`).
   - Switch simulation speed to `2.0x`, `3.5x`, or `5.0x` to demonstrate high-speed transit runs across Chennai.
   - Stop or resume the simulation loop and verify the status reflected across the dashboard.
5. **Driver Onboard Terminal:**
   - Open `/driver/dashboard` (logged in as driver).
   - View assigned bus `BUS-101` and click **Send GPS Ping** or **Fill from Browser GPS** to transmit live coordinates.

---

## 10. Verification & Test Summary

The automated test suite contains **28 tests** across unit, service, security, and integration layers:
- `BusTrackingApplicationTests`: Spring Boot context bootstrap and JPA scanning.
- `HaversineDistanceCalculatorTest` (5 tests): Great-Circle distance, bearing azimuth, interpolation midpoint, travel time estimation.
- `EtaCalculationServiceTest` (2 tests): Multi-stop route ETA prediction, single stop distance calculation.
- `BusServiceTest` (4 tests): Bus retrieval, capacity constraints, occupancy calculation, crowd status categorization.
- `RouteServiceTest` (3 tests): Active routes filtering, keyword search, route persistence.
- `TripServiceTest` (3 tests): Trip creation, dispatch status progression (`SCHEDULED` ➔ `IN_PROGRESS` ➔ `COMPLETED`).
- `GpsSimulatorServiceTest` (2 tests): Scheduled thread lifecycle start/stop, velocity multiplier bounds clamping.
- `PassengerControllerTest` (4 tests): MVC endpoint status 200, view name verification, model attributes for Home, Track, Routes, and Search.
- `AdminSecurityTest` (4 tests): Spring Security 401/403 redirects for unauthenticated commuters, role authorization for `ROLE_ADMIN`.

**Test Execution Result:**  
`[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0` — **BUILD SUCCESS**

---

## 11. Known Limitations & Future Enhancements

- **Physical GPS Integration:** Currently supports simulated coordinates and manual driver HTTP beacons; ready for direct integration with hardware OBD-II / GSM telemetry devices.
- **Historical Playback:** Telemetry is persisted in `bus_locations`; future work can include interactive trip playback sliders.
- **Push Notifications:** WebSockets deliver in-page alerts; browser Web Push / SMS notifications for arrival warnings (e.g., "Bus is 2 stops away") can be added.
