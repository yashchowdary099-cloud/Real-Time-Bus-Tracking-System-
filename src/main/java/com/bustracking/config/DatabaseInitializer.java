package com.bustracking.config;

import com.bustracking.entity.*;
import com.bustracking.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;
    private final TripRepository tripRepository;
    private final BusLocationRepository busLocationRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(RoleRepository roleRepository,
                               UserRepository userRepository,
                               DriverRepository driverRepository,
                               BusRepository busRepository,
                               RouteRepository routeRepository,
                               StopRepository stopRepository,
                               RouteStopRepository routeStopRepository,
                               TripRepository tripRepository,
                               BusLocationRepository busLocationRepository,
                               PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
        this.tripRepository = tripRepository;
        this.busLocationRepository = busLocationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking database initialization status...");

        // 1. Initialize Roles
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role driverRole = roleRepository.findByName("ROLE_DRIVER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_DRIVER")));
        Role passengerRole = roleRepository.findByName("ROLE_PASSENGER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_PASSENGER")));

        // 2. Initialize Default Users
        User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User("admin", passwordEncoder.encode("admin123"), "Administrator", "admin@bustracking.edu", "+91 98765 00001");
            u.addRole(adminRole);
            return userRepository.save(u);
        });

        User driverUser1 = userRepository.findByUsername("driver1").orElseGet(() -> {
            User u = new User("driver1", passwordEncoder.encode("driver123"), "Ramesh Kumar (Driver)", "driver1@bustracking.edu", "+91 98765 43210");
            u.addRole(driverRole);
            return userRepository.save(u);
        });

        User driverUser2 = userRepository.findByUsername("driver2").orElseGet(() -> {
            User u = new User("driver2", passwordEncoder.encode("driver123"), "Suresh Balaji (Driver)", "driver2@bustracking.edu", "+91 98765 43211");
            u.addRole(driverRole);
            return userRepository.save(u);
        });

        userRepository.findByUsername("student1").orElseGet(() -> {
            User u = new User("student1", passwordEncoder.encode("student123"), "Tanmay Marolia (Student)", "student1@sathyabama.ac.in", "+91 98765 99999");
            u.addRole(passengerRole);
            return userRepository.save(u);
        });

        // 3. Initialize Drivers
        Driver driver1 = driverRepository.findByLicenseNumber("DL-TN-01-2015-00123").orElseGet(() -> {
            Driver d = new Driver("DL-TN-01-2015-00123", "Ramesh Kumar", "+91 98765 43210", DriverStatus.ON_TRIP);
            d.setUser(driverUser1);
            return driverRepository.save(d);
        });

        Driver driver2 = driverRepository.findByLicenseNumber("DL-TN-07-2018-00456").orElseGet(() -> {
            Driver d = new Driver("DL-TN-07-2018-00456", "Suresh Balaji", "+91 98765 43211", DriverStatus.ON_TRIP);
            d.setUser(driverUser2);
            return driverRepository.save(d);
        });

        driverRepository.findByLicenseNumber("DL-TN-14-2020-00789").orElseGet(() -> {
            Driver d = new Driver("DL-TN-14-2020-00789", "Murugan Sundaram", "+91 98765 43212", DriverStatus.AVAILABLE);
            return driverRepository.save(d);
        });

        // 4. Initialize Stops (Chennai / Sathyabama OMR Corridor)
        Stop s1 = getOrCreateStop("Chennai Central Station", 13.0827, 80.2707, "Main railway & metro transit hub");
        Stop s2 = getOrCreateStop("Guindy Industrial Estate", 13.0067, 80.2030, "Key junction near Kathipara Flyover");
        Stop s3 = getOrCreateStop("Adyar Depot", 13.0012, 80.2565, "Adyar bus terminus & residential hub");
        Stop s4 = getOrCreateStop("SRP Tools (OMR Entry)", 12.9863, 80.2452, "Entry junction to Rajiv Gandhi IT Expressway (OMR)");
        Stop s5 = getOrCreateStop("Kandanchavadi IT Corridor", 12.9664, 80.2421, "IT tech park hub on OMR");
        Stop s6 = getOrCreateStop("Thoraipakkam Bus Stop", 12.9352, 80.2335, "Thoraipakkam toll & junction");
        Stop s7 = getOrCreateStop("Sholinganallur Junction", 12.9010, 80.2279, "Major crossroads connecting ECR and Tambaram");
        Stop s8 = getOrCreateStop("Sathyabama University Campus", 12.8718, 80.2206, "Main Gate, Sathyabama Institute of Science and Technology, Jeppiaar Nagar, OMR");
        Stop s9 = getOrCreateStop("Tambaram Bus Stand", 12.9249, 80.1000, "South suburban rail and bus interchange");
        Stop s10 = getOrCreateStop("Chromepet Grand Southern Trunk", 12.9516, 80.1462, "GST road arterial stop");
        Stop s11 = getOrCreateStop("Medavakkam Koot Road", 12.9197, 80.1873, "Medavakkam prime cross-junction");
        Stop s12 = getOrCreateStop("Perumbakkam Junction", 12.9056, 80.2012, "Near Global Hospitals & residential townships");

        // 5. Initialize Routes
        Route r1 = routeRepository.findByRouteNumber("ROUTE-101").orElseGet(() -> {
            Route r = new Route("ROUTE-101", "Central Station to Sathyabama Campus (OMR Express)", "Chennai Central Station", "Sathyabama University Campus", 28.5, 65, true);
            Route saved = routeRepository.save(r);

            routeStopRepository.save(new RouteStop(saved, s1, 1, 0.0, 0));
            routeStopRepository.save(new RouteStop(saved, s2, 2, 11.0, 22));
            routeStopRepository.save(new RouteStop(saved, s4, 3, 15.5, 32));
            routeStopRepository.save(new RouteStop(saved, s5, 4, 18.2, 39));
            routeStopRepository.save(new RouteStop(saved, s6, 5, 21.8, 48));
            routeStopRepository.save(new RouteStop(saved, s7, 6, 25.4, 57));
            routeStopRepository.save(new RouteStop(saved, s8, 7, 28.5, 65));
            return saved;
        });

        Route r2 = routeRepository.findByRouteNumber("ROUTE-102").orElseGet(() -> {
            Route r = new Route("ROUTE-102", "Tambaram to Sathyabama Campus (Medavakkam Feeder)", "Tambaram Bus Stand", "Sathyabama University Campus", 18.2, 45, true);
            Route saved = routeRepository.save(r);

            routeStopRepository.save(new RouteStop(saved, s9, 1, 0.0, 0));
            routeStopRepository.save(new RouteStop(saved, s10, 2, 4.2, 10));
            routeStopRepository.save(new RouteStop(saved, s11, 3, 9.8, 24));
            routeStopRepository.save(new RouteStop(saved, s12, 4, 13.1, 32));
            routeStopRepository.save(new RouteStop(saved, s7, 5, 15.6, 38));
            routeStopRepository.save(new RouteStop(saved, s8, 6, 18.2, 45));
            return saved;
        });

        // 6. Initialize Buses
        Bus bus1 = busRepository.findByBusNumber("BUS-101").orElseGet(() -> 
            busRepository.save(new Bus("BUS-101", "TN-01-AN-2024", 45, 28, BusStatus.IN_SERVICE, "Tata Starbus Ultra AC", true))
        );

        Bus bus2 = busRepository.findByBusNumber("BUS-102").orElseGet(() -> 
            busRepository.save(new Bus("BUS-102", "TN-07-BL-4590", 50, 36, BusStatus.IN_SERVICE, "Ashok Leyland Lynx Express", true))
        );

        Bus bus3 = busRepository.findByBusNumber("BUS-103").orElseGet(() -> 
            busRepository.save(new Bus("BUS-103", "TN-14-CZ-8821", 40, 15, BusStatus.IN_SERVICE, "Eicher Starline Transit", true))
        );

        busRepository.findByBusNumber("BUS-104").orElseGet(() -> 
            busRepository.save(new Bus("BUS-104", "TN-22-EX-1104", 45, 0, BusStatus.MAINTENANCE, "BharatBenz Tourliner", false))
        );

        // 7. Initialize Trips
        Trip trip1 = tripRepository.findByTripNumber("TRIP-101-AM").orElseGet(() -> {
            Trip t = new Trip("TRIP-101-AM", bus1, r1, driver1, TripStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(20));
            t.setCurrentStop(s4);
            t.setNextStop(s5);
            return tripRepository.save(t);
        });

        Trip trip2 = tripRepository.findByTripNumber("TRIP-102-AM").orElseGet(() -> {
            Trip t = new Trip("TRIP-102-AM", bus2, r2, driver2, TripStatus.IN_PROGRESS, LocalDateTime.now().minusMinutes(15));
            t.setCurrentStop(s11);
            t.setNextStop(s12);
            return tripRepository.save(t);
        });

        // 8. Initialize Initial Bus Locations
        if (busLocationRepository.findLatestByBusId(bus1.getId()).isEmpty()) {
            busLocationRepository.save(new BusLocation(bus1, trip1, 12.9863, 80.2452, 38.5, 182.0, true, LocalDateTime.now()));
        }
        if (busLocationRepository.findLatestByBusId(bus2.getId()).isEmpty()) {
            busLocationRepository.save(new BusLocation(bus2, trip2, 12.9197, 80.1873, 42.0, 115.0, true, LocalDateTime.now()));
        }

        log.info("Database initialized successfully with default users, buses, routes, and active trips.");
    }

    private Stop getOrCreateStop(String name, double lat, double lon, String desc) {
        List<Stop> existing = stopRepository.searchStops(name);
        for (Stop s : existing) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return stopRepository.save(new Stop(name, lat, lon, desc));
    }
}
