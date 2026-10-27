package com.bustracking.gps;

import com.bustracking.dto.SimulationStatusDto;
import com.bustracking.entity.Bus;
import com.bustracking.entity.RouteStop;
import com.bustracking.entity.Trip;
import com.bustracking.repository.BusRepository;
import com.bustracking.repository.RouteStopRepository;
import com.bustracking.repository.TripRepository;
import com.bustracking.service.BusLocationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * Java Multithreaded GPS Simulation Service.
 * Fulfills CO3 (multithreading and Runnable task scheduling) by simulating live telemetry
 * feeds along real transit coordinates without freezing server request processing.
 */
@Service
public class GpsSimulatorService {

    private static final Logger log = LoggerFactory.getLogger(GpsSimulatorService.class);

    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final RouteStopRepository routeStopRepository;
    private final BusLocationService busLocationService;

    @Value("${app.simulation.auto-start:true}")
    private boolean autoStart;

    @Value("${app.simulation.interval-ms:2000}")
    private long intervalMs = 2000;

    @Value("${app.simulation.speed-multiplier:1.0}")
    private double speedMultiplier = 1.0;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> simulationTask;
    private volatile boolean isRunning = false;

    // Per-bus simulation trajectory state
    private final ConcurrentHashMap<Long, BusSimState> busStates = new ConcurrentHashMap<>();

    public GpsSimulatorService(BusRepository busRepository,
                               TripRepository tripRepository,
                               RouteStopRepository routeStopRepository,
                               BusLocationService busLocationService) {
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
        this.routeStopRepository = routeStopRepository;
        this.busLocationService = busLocationService;
    }

    @PostConstruct
    public void init() {
        if (autoStart) {
            log.info("Auto-starting Java GPS simulation engine (interval: {} ms)...", intervalMs);
            startSimulation();
        }
    }

    @PreDestroy
    public void cleanup() {
        stopSimulation();
    }

    /**
     * Starts the multithreaded simulation loop using ScheduledExecutorService.
     */
    public synchronized void startSimulation() {
        if (isRunning) {
            log.info("GPS simulation is already running.");
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "JavaGpsSimulatorThread");
            t.setDaemon(true);
            return t;
        });

        long effectiveInterval = intervalMs > 0 ? intervalMs : 2000;
        simulationTask = scheduler.scheduleAtFixedRate(
                this::runSimulationTick,
                1000,
                effectiveInterval,
                TimeUnit.MILLISECONDS
        );

        isRunning = true;
        log.info("Java GPS simulation engine started successfully.");
    }

    /**
     * Stops the simulation loop cleanly.
     */
    public synchronized void stopSimulation() {
        if (!isRunning) {
            return;
        }
        if (simulationTask != null) {
            simulationTask.cancel(true);
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        isRunning = false;
        log.info("Java GPS simulation engine stopped.");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public SimulationStatusDto getSimulationStatus() {
        return new SimulationStatusDto(
                isRunning,
                busStates.size(),
                intervalMs,
                speedMultiplier,
                isRunning ? "GPS Simulation actively broadcasting telemetry" : "GPS Simulation is currently paused"
        );
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = Math.max(0.2, Math.min(5.0, speedMultiplier));
    }

    /**
     * Main simulation tick executed periodically on a background Java thread.
     */
    private void runSimulationTick() {
        try {
            List<Trip> activeTrips = tripRepository.findAllActiveTrips();

            for (Trip trip : activeTrips) {
                Bus bus = trip.getBus();
                if (bus == null || !bus.isSimulated()) {
                    continue;
                }

                BusSimState state = busStates.computeIfAbsent(bus.getId(), id -> initBusState(trip));
                if (state == null || state.waypoints.size() < 2) {
                    continue;
                }

                // Advance location along waypoints
                advanceBusPosition(trip, bus, state);
            }
        } catch (Exception e) {
            log.error("Error occurred in GPS simulation tick: {}", e.getMessage(), e);
        }
    }

    private BusSimState initBusState(Trip trip) {
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopOrderAsc(trip.getRoute().getId());
        if (routeStops.size() < 2) {
            return null;
        }

        List<Coordinate> coords = new ArrayList<>();
        for (RouteStop rs : routeStops) {
            coords.add(new Coordinate(rs.getStop().getLatitude(), rs.getStop().getLongitude()));
        }

        BusSimState state = new BusSimState();
        state.waypoints = coords;
        state.currentIndex = 0;
        state.progress = 0.0;
        state.forward = true;
        state.speedKmh = 35.0 + (ThreadLocalRandom.current().nextDouble() * 10.0);
        return state;
    }

    private void advanceBusPosition(Trip trip, Bus bus, BusSimState state) {
        int idx = state.currentIndex;
        int nextIdx = state.forward ? idx + 1 : idx - 1;

        if (nextIdx >= state.waypoints.size()) {
            // Reached destination, turn back
            state.forward = false;
            state.currentIndex = state.waypoints.size() - 1;
            nextIdx = state.currentIndex - 1;
        } else if (nextIdx < 0) {
            // Reached origin, turn forward
            state.forward = true;
            state.currentIndex = 0;
            nextIdx = 1;
        }

        Coordinate currentWp = state.waypoints.get(state.currentIndex);
        Coordinate nextWp = state.waypoints.get(nextIdx);

        double legDistKm = HaversineDistanceCalculator.calculateDistanceKm(
                currentWp.getLatitude(), currentWp.getLongitude(),
                nextWp.getLatitude(), nextWp.getLongitude()
        );

        if (legDistKm <= 0.0001) {
            state.currentIndex = nextIdx;
            return;
        }

        // Distance traveled in this time interval (seconds to hours conversion)
        double elapsedHours = (intervalMs / 1000.0) / 3600.0;
        double effectiveSpeed = state.speedKmh * speedMultiplier;
        double distanceCoveredKm = effectiveSpeed * elapsedHours;

        state.progress += (distanceCoveredKm / legDistKm);

        if (state.progress >= 1.0) {
            // Arrived at next waypoint
            state.progress = 0.0;
            state.currentIndex = nextIdx;

            // Randomize speed slightly for realism
            state.speedKmh = 30.0 + (ThreadLocalRandom.current().nextDouble() * 15.0);

            // Fluctuate bus occupancy slightly at stops
            int change = ThreadLocalRandom.current().nextInt(-2, 4);
            int newOcc = Math.max(5, Math.min(bus.getCapacity(), bus.getCurrentOccupancy() + change));
            bus.setCurrentOccupancy(newOcc);
            busRepository.save(bus);

            // Update trip current and next stop
            updateTripStopProgress(trip, state.currentIndex);
        }

        // Calculate interpolated current coordinates
        int fromIdx = state.currentIndex;
        int toIdx = state.forward ? Math.min(state.waypoints.size() - 1, fromIdx + 1) : Math.max(0, fromIdx - 1);

        Coordinate c1 = state.waypoints.get(fromIdx);
        Coordinate c2 = state.waypoints.get(toIdx);

        Coordinate interpolated = HaversineDistanceCalculator.interpolate(c1, c2, state.progress);
        double bearing = HaversineDistanceCalculator.calculateBearing(
                c1.getLatitude(), c1.getLongitude(),
                c2.getLatitude(), c2.getLongitude()
        );

        // Persist update and broadcast via WebSocket
        busLocationService.updateBusLocation(
                bus.getId(),
                interpolated.getLatitude(),
                interpolated.getLongitude(),
                effectiveSpeed,
                bearing,
                true
        );
    }

    private void updateTripStopProgress(Trip trip, int stopOrderZeroIndexed) {
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopOrderAsc(trip.getRoute().getId());
        if (stopOrderZeroIndexed < stops.size()) {
            trip.setCurrentStop(stops.get(stopOrderZeroIndexed).getStop());
            if (stopOrderZeroIndexed + 1 < stops.size()) {
                trip.setNextStop(stops.get(stopOrderZeroIndexed + 1).getStop());
            } else {
                trip.setNextStop(null);
            }
            tripRepository.save(trip);
        }
    }

    private static class BusSimState {
        List<Coordinate> waypoints;
        int currentIndex;
        double progress;
        boolean forward;
        double speedKmh;
    }
}
