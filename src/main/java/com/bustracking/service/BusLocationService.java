package com.bustracking.service;

import com.bustracking.dto.BusLocationDto;
import com.bustracking.dto.EtaResponseDto;
import com.bustracking.entity.Bus;
import com.bustracking.entity.BusLocation;
import com.bustracking.entity.Trip;
import com.bustracking.repository.BusLocationRepository;
import com.bustracking.repository.BusRepository;
import com.bustracking.repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusLocationService {

    private static final Logger log = LoggerFactory.getLogger(BusLocationService.class);

    private final BusLocationRepository busLocationRepository;
    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EtaCalculationService etaCalculationService;

    public BusLocationService(BusLocationRepository busLocationRepository,
                              BusRepository busRepository,
                              TripRepository tripRepository,
                              SimpMessagingTemplate messagingTemplate,
                              EtaCalculationService etaCalculationService) {
        this.busLocationRepository = busLocationRepository;
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
        this.messagingTemplate = messagingTemplate;
        this.etaCalculationService = etaCalculationService;
    }

    /**
     * Records a new bus location, persists to database, and publishes via WebSocket.
     */
    @Transactional
    public BusLocationDto updateBusLocation(Long busId,
                                            Double latitude,
                                            Double longitude,
                                            Double speedKmh,
                                            Double headingDegrees,
                                            boolean isSimulated) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new IllegalArgumentException("Bus not found with id: " + busId));

        Optional<Trip> activeTripOpt = tripRepository.findActiveTripByBusId(busId);
        Trip trip = activeTripOpt.orElse(null);

        BusLocation location = new BusLocation(
                bus,
                trip,
                latitude,
                longitude,
                speedKmh != null ? speedKmh : 0.0,
                headingDegrees != null ? headingDegrees : 0.0,
                isSimulated,
                LocalDateTime.now()
        );

        BusLocation saved = busLocationRepository.save(location);

        BusLocationDto dto = convertToDto(saved, bus, trip);

        // Broadcast to WebSocket STOMP subscribers
        try {
            messagingTemplate.convertAndSend("/topic/bus-locations", dto);
            messagingTemplate.convertAndSend("/topic/bus-location/" + busId, dto);
        } catch (Exception e) {
            log.warn("Failed to publish WebSocket message for bus {}: {}", busId, e.getMessage());
        }

        return dto;
    }

    /**
     * Retrieves the latest reported location for a bus.
     */
    @Transactional(readOnly = true)
    public Optional<BusLocationDto> getLatestLocation(Long busId) {
        return busLocationRepository.findLatestByBusId(busId).map(loc -> {
            Bus bus = loc.getBus();
            Trip trip = loc.getTrip();
            return convertToDto(loc, bus, trip);
        });
    }

    /**
     * Retrieves the latest locations for all active buses.
     */
    @Transactional(readOnly = true)
    public List<BusLocationDto> getAllLatestLocations() {
        List<Bus> buses = busRepository.findAll();
        List<BusLocationDto> list = new ArrayList<>();
        for (Bus bus : buses) {
            busLocationRepository.findLatestByBusId(bus.getId()).ifPresent(loc -> {
                list.add(convertToDto(loc, bus, loc.getTrip()));
            });
        }
        return list;
    }

    /**
     * Converts entity to BusLocationDto and enriches with trip, route, and ETA details.
     */
    public BusLocationDto convertToDto(BusLocation loc, Bus bus, Trip trip) {
        BusLocationDto dto = new BusLocationDto();
        dto.setBusId(bus.getId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setRegistrationNumber(bus.getRegistrationNumber());
        dto.setLatitude(loc.getLatitude());
        dto.setLongitude(loc.getLongitude());
        dto.setSpeedKmh(Math.round(loc.getSpeedKmh() * 10.0) / 10.0);
        dto.setHeadingDegrees(Math.round(loc.getHeadingDegrees() * 10.0) / 10.0);
        dto.setSimulated(loc.isSimulated());
        dto.setTimestamp(loc.getTimestamp());
        dto.setCapacity(bus.getCapacity());
        dto.setCurrentOccupancy(bus.getCurrentOccupancy());
        dto.setCrowdStatus(bus.getCrowdStatus());

        if (trip != null) {
            dto.setTripId(trip.getId());
            dto.setTripNumber(trip.getTripNumber());
            if (trip.getRoute() != null) {
                dto.setRouteId(trip.getRoute().getId());
                dto.setRouteNumber(trip.getRoute().getRouteNumber());
                dto.setRouteName(trip.getRoute().getName());
            }
            if (trip.getCurrentStop() != null) {
                dto.setCurrentStopName(trip.getCurrentStop().getName());
            }
            if (trip.getNextStop() != null) {
                dto.setNextStopName(trip.getNextStop().getName());
                EtaResponseDto eta = etaCalculationService.calculateEtaToStop(
                        trip.getNextStop(), loc.getLatitude(), loc.getLongitude(), loc.getSpeedKmh()
                );
                dto.setEtaNextStopMinutes(eta.getEtaMinutes());
            }
        }

        return dto;
    }
}
