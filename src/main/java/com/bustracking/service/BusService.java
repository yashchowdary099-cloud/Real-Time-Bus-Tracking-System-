package com.bustracking.service;

import com.bustracking.dto.BusDto;
import com.bustracking.entity.Bus;
import com.bustracking.entity.BusStatus;
import com.bustracking.entity.Trip;
import com.bustracking.exception.ResourceNotFoundException;
import com.bustracking.repository.BusLocationRepository;
import com.bustracking.repository.BusRepository;
import com.bustracking.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusService {

    private final BusRepository busRepository;
    private final TripRepository tripRepository;
    private final BusLocationRepository busLocationRepository;

    public BusService(BusRepository busRepository,
                      TripRepository tripRepository,
                      BusLocationRepository busLocationRepository) {
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
        this.busLocationRepository = busLocationRepository;
    }

    @Transactional(readOnly = true)
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BusDto> getAllBusDtos() {
        return busRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Bus getBusById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public BusDto getBusDtoById(Long id) {
        return convertToDto(getBusById(id));
    }

    @Transactional
    public Bus saveBus(Bus bus) {
        return busRepository.save(bus);
    }

    @Transactional
    public Bus updateBus(Long id, Bus updated) {
        Bus existing = getBusById(id);
        existing.setBusNumber(updated.getBusNumber());
        existing.setRegistrationNumber(updated.getRegistrationNumber());
        existing.setCapacity(updated.getCapacity());
        existing.setCurrentOccupancy(updated.getCurrentOccupancy());
        existing.setStatus(updated.getStatus());
        existing.setModel(updated.getModel());
        existing.setSimulated(updated.isSimulated());
        return busRepository.save(existing);
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = getBusById(id);
        busLocationRepository.deleteByBusId(id);
        busRepository.delete(bus);
    }

    @Transactional
    public void updateOccupancy(Long busId, int occupancy) {
        Bus bus = getBusById(busId);
        bus.setCurrentOccupancy(occupancy);
        busRepository.save(bus);
    }

    @Transactional
    public void updateStatus(Long busId, BusStatus status) {
        Bus bus = getBusById(busId);
        bus.setStatus(status);
        busRepository.save(bus);
    }

    public BusDto convertToDto(Bus bus) {
        BusDto dto = new BusDto();
        dto.setId(bus.getId());
        dto.setBusNumber(bus.getBusNumber());
        dto.setRegistrationNumber(bus.getRegistrationNumber());
        dto.setCapacity(bus.getCapacity());
        dto.setCurrentOccupancy(bus.getCurrentOccupancy());
        dto.setStatus(bus.getStatus());
        dto.setModel(bus.getModel());
        dto.setSimulated(bus.isSimulated());
        dto.setAvailableSeats(bus.getAvailableSeats());
        dto.setOccupancyPercentage(bus.getOccupancyPercentage());
        dto.setCrowdStatus(bus.getCrowdStatus());

        Optional<Trip> trip = tripRepository.findActiveTripByBusId(bus.getId());
        if (trip.isPresent()) {
            dto.setActiveTripId(trip.get().getId());
            if (trip.get().getRoute() != null) {
                dto.setActiveRouteName(trip.get().getRoute().getRouteNumber() + " - " + trip.get().getRoute().getName());
            }
        }

        return dto;
    }
}
