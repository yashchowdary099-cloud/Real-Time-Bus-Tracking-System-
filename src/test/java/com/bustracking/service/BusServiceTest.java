package com.bustracking.service;

import com.bustracking.dto.BusDto;
import com.bustracking.entity.Bus;
import com.bustracking.entity.BusStatus;
import com.bustracking.repository.BusLocationRepository;
import com.bustracking.repository.BusRepository;
import com.bustracking.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private BusLocationRepository busLocationRepository;

    @InjectMocks
    private BusService busService;

    private Bus testBus;

    @BeforeEach
    void setUp() {
        testBus = new Bus("BUS-201", "TN-09-XX-9999", 50, 20, BusStatus.IN_SERVICE, "Ashok Leyland", true);
        testBus.setId(1L);
    }

    @Test
    void testGetAllBuses() {
        when(busRepository.findAll()).thenReturn(List.of(testBus));

        List<Bus> buses = busService.getAllBuses();
        assertEquals(1, buses.size());
        assertEquals("BUS-201", buses.get(0).getBusNumber());
    }

    @Test
    void testGetBusById() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));

        Bus result = busService.getBusById(1L);
        assertNotNull(result);
        assertEquals(50, result.getCapacity());
        assertEquals(30, result.getAvailableSeats());
    }

    @Test
    void testUpdateOccupancy() {
        when(busRepository.findById(1L)).thenReturn(Optional.of(testBus));
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);

        busService.updateOccupancy(1L, 45);

        assertEquals(45, testBus.getCurrentOccupancy());
        assertEquals(5, testBus.getAvailableSeats());
        assertEquals("Crowded", testBus.getCrowdStatus());
    }

    @Test
    void testConvertToDto() {
        when(tripRepository.findActiveTripByBusId(1L)).thenReturn(Optional.empty());

        BusDto dto = busService.convertToDto(testBus);

        assertEquals("BUS-201", dto.getBusNumber());
        assertEquals(50, dto.getCapacity());
        assertEquals(20, dto.getCurrentOccupancy());
        assertEquals(40, dto.getOccupancyPercentage());
        assertEquals("Low Crowding", dto.getCrowdStatus());
    }
}
