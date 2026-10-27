package com.bustracking.service;

import com.bustracking.entity.*;
import com.bustracking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private BusRepository busRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private StopRepository stopRepository;
    @Mock
    private RouteStopRepository routeStopRepository;

    @InjectMocks
    private TripService tripService;

    private Bus bus;
    private Route route;
    private Driver driver;
    private Trip trip;

    @BeforeEach
    void setUp() {
        bus = new Bus("BUS-101", "TN-01-AN-2024", 45, 0, BusStatus.IN_SERVICE, "Tata", true);
        bus.setId(10L);
        route = new Route("ROUTE-101", "OMR", "Origin", "Dest", 20.0, 45, true);
        route.setId(20L);
        driver = new Driver("DL-12345", "Ramesh", "+91 98765 43210", DriverStatus.AVAILABLE);
        driver.setId(30L);
        trip = new Trip("TRIP-001", bus, route, driver, TripStatus.SCHEDULED, null);
        trip.setId(100L);
    }

    @Test
    void testCreateTrip() {
        when(busRepository.findById(10L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(20L)).thenReturn(Optional.of(route));
        when(driverRepository.findById(30L)).thenReturn(Optional.of(driver));
        when(routeStopRepository.findByRouteIdOrderByStopOrderAsc(20L)).thenReturn(Collections.emptyList());
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip created = tripService.createTrip("TRIP-001", 10L, 20L, 30L);
        assertNotNull(created);
        assertEquals("TRIP-001", created.getTripNumber());
        assertEquals(TripStatus.SCHEDULED, created.getStatus());
    }

    @Test
    void testStartTrip() {
        when(tripRepository.findById(100L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip started = tripService.startTrip(100L);

        assertEquals(TripStatus.IN_PROGRESS, started.getStatus());
        assertNotNull(started.getStartTime());
        assertEquals(DriverStatus.ON_TRIP, driver.getStatus());
    }

    @Test
    void testCompleteTrip() {
        trip.setStatus(TripStatus.IN_PROGRESS);
        when(tripRepository.findById(100L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip completed = tripService.completeTrip(100L);

        assertEquals(TripStatus.COMPLETED, completed.getStatus());
        assertNotNull(completed.getEndTime());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }
}
