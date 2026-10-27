package com.bustracking.gps;

import com.bustracking.dto.SimulationStatusDto;
import com.bustracking.repository.BusRepository;
import com.bustracking.repository.RouteStopRepository;
import com.bustracking.repository.TripRepository;
import com.bustracking.service.BusLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GpsSimulatorServiceTest {

    @Mock
    private BusRepository busRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private RouteStopRepository routeStopRepository;
    @Mock
    private BusLocationService busLocationService;

    @InjectMocks
    private GpsSimulatorService gpsSimulatorService;

    @Test
    void testStartAndStopSimulation() {
        assertFalse(gpsSimulatorService.isRunning());

        gpsSimulatorService.startSimulation();
        assertTrue(gpsSimulatorService.isRunning());

        SimulationStatusDto status = gpsSimulatorService.getSimulationStatus();
        assertNotNull(status);
        assertTrue(status.isRunning());

        gpsSimulatorService.stopSimulation();
        assertFalse(gpsSimulatorService.isRunning());
    }

    @Test
    void testSpeedMultiplierClamping() {
        gpsSimulatorService.setSpeedMultiplier(10.0); // should clamp to 5.0
        SimulationStatusDto status = gpsSimulatorService.getSimulationStatus();
        assertEquals(5.0, status.getSpeedMultiplier(), 0.01);

        gpsSimulatorService.setSpeedMultiplier(0.01); // should clamp to 0.2
        status = gpsSimulatorService.getSimulationStatus();
        assertEquals(0.2, status.getSpeedMultiplier(), 0.01);
    }
}
