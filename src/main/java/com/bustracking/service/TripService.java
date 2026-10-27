package com.bustracking.service;

import com.bustracking.dto.TripDto;
import com.bustracking.entity.*;
import com.bustracking.exception.ResourceNotFoundException;
import com.bustracking.repository.BusRepository;
import com.bustracking.repository.DriverRepository;
import com.bustracking.repository.RouteRepository;
import com.bustracking.repository.RouteStopRepository;
import com.bustracking.repository.StopRepository;
import com.bustracking.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;

    public TripService(TripRepository tripRepository,
                       BusRepository busRepository,
                       RouteRepository routeRepository,
                       DriverRepository driverRepository,
                       StopRepository stopRepository,
                       RouteStopRepository routeStopRepository) {
        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.driverRepository = driverRepository;
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
    }

    @Transactional(readOnly = true)
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TripDto> getAllTripDtos() {
        return tripRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Trip> getActiveTrips() {
        return tripRepository.findAllActiveTrips();
    }

    @Transactional(readOnly = true)
    public List<TripDto> getActiveTripDtos() {
        return tripRepository.findAllActiveTrips().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public TripDto getTripDtoById(Long id) {
        return convertToDto(getTripById(id));
    }

    @Transactional(readOnly = true)
    public Optional<Trip> getActiveTripByBusId(Long busId) {
        return tripRepository.findActiveTripByBusId(busId);
    }

    @Transactional(readOnly = true)
    public Optional<Trip> getActiveTripByDriverId(Long driverId) {
        return tripRepository.findActiveTripByDriverId(driverId);
    }

    @Transactional
    public Trip createTrip(String tripNumber, Long busId, Long routeId, Long driverId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + busId));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + routeId));
        Driver driver = driverId != null ? driverRepository.findById(driverId).orElse(null) : null;

        Trip trip = new Trip(tripNumber, bus, route, driver, TripStatus.SCHEDULED, null);

        // Pre-assign first and second stops
        List<RouteStop> stops = routeStopRepository.findByRouteIdOrderByStopOrderAsc(route.getId());
        if (!stops.isEmpty()) {
            trip.setCurrentStop(stops.get(0).getStop());
            if (stops.size() > 1) {
                trip.setNextStop(stops.get(1).getStop());
            }
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip startTrip(Long tripId) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setStartTime(LocalDateTime.now());

        if (trip.getBus() != null) {
            trip.getBus().setStatus(BusStatus.IN_SERVICE);
            busRepository.save(trip.getBus());
        }
        if (trip.getDriver() != null) {
            trip.getDriver().setStatus(DriverStatus.ON_TRIP);
            driverRepository.save(trip.getDriver());
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip completeTrip(Long tripId) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());

        if (trip.getBus() != null) {
            trip.getBus().setCurrentOccupancy(0);
            busRepository.save(trip.getBus());
        }
        if (trip.getDriver() != null) {
            trip.getDriver().setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(trip.getDriver());
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip cancelTrip(Long tripId) {
        Trip trip = getTripById(tripId);
        trip.setStatus(TripStatus.CANCELLED);
        trip.setEndTime(LocalDateTime.now());

        if (trip.getDriver() != null) {
            trip.getDriver().setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(trip.getDriver());
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = getTripById(id);
        tripRepository.delete(trip);
    }

    @Transactional
    public void updateTripStops(Long tripId, Long currentStopId, Long nextStopId) {
        Trip trip = getTripById(tripId);
        if (currentStopId != null) {
            stopRepository.findById(currentStopId).ifPresent(trip::setCurrentStop);
        }
        if (nextStopId != null) {
            stopRepository.findById(nextStopId).ifPresent(trip::setNextStop);
        }
        tripRepository.save(trip);
    }

    public TripDto convertToDto(Trip trip) {
        TripDto dto = new TripDto();
        dto.setId(trip.getId());
        dto.setTripNumber(trip.getTripNumber());
        dto.setStatus(trip.getStatus());
        dto.setStartTime(trip.getStartTime());
        dto.setEndTime(trip.getEndTime());

        if (trip.getBus() != null) {
            dto.setBusId(trip.getBus().getId());
            dto.setBusNumber(trip.getBus().getBusNumber());
        }
        if (trip.getRoute() != null) {
            dto.setRouteId(trip.getRoute().getId());
            dto.setRouteNumber(trip.getRoute().getRouteNumber());
            dto.setRouteName(trip.getRoute().getName());
        }
        if (trip.getDriver() != null) {
            dto.setDriverId(trip.getDriver().getId());
            dto.setDriverName(trip.getDriver().getFullName());
        }
        if (trip.getCurrentStop() != null) {
            dto.setCurrentStopName(trip.getCurrentStop().getName());
        }
        if (trip.getNextStop() != null) {
            dto.setNextStopName(trip.getNextStop().getName());
        }

        return dto;
    }
}
