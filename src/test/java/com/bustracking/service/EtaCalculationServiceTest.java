package com.bustracking.service;

import com.bustracking.dto.EtaResponseDto;
import com.bustracking.entity.Route;
import com.bustracking.entity.RouteStop;
import com.bustracking.entity.Stop;
import com.bustracking.repository.RouteStopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EtaCalculationServiceTest {

    @Mock
    private RouteStopRepository routeStopRepository;

    @InjectMocks
    private EtaCalculationService etaCalculationService;

    private Route testRoute;
    private Stop stop1;
    private Stop stop2;
    private Stop stop3;

    @BeforeEach
    void setUp() {
        testRoute = new Route("ROUTE-101", "OMR Express", "Central", "Sathyabama", 28.5, 65, true);
        testRoute.setId(1L);

        stop1 = new Stop("Central", 13.0827, 80.2707, "Station");
        stop1.setId(101L);
        stop2 = new Stop("Guindy", 13.0067, 80.2030, "Estate");
        stop2.setId(102L);
        stop3 = new Stop("Sathyabama", 12.8718, 80.2206, "Campus");
        stop3.setId(103L);
    }

    @Test
    void testCalculateEtasForRemainingStops() {
        RouteStop rs1 = new RouteStop(testRoute, stop1, 1, 0.0, 0);
        RouteStop rs2 = new RouteStop(testRoute, stop2, 2, 11.0, 25);
        RouteStop rs3 = new RouteStop(testRoute, stop3, 3, 28.5, 65);

        when(routeStopRepository.findByRouteIdOrderByStopOrderAsc(1L)).thenReturn(List.of(rs1, rs2, rs3));

        // Current bus position near Guindy (stop 2), stopOrder 2 visited
        List<EtaResponseDto> etas = etaCalculationService.calculateEtasForRemainingStops(
                testRoute, 13.0067, 80.2030, 35.0, 2
        );

        assertNotNull(etas);
        assertEquals(1, etas.size(), "Should have 1 remaining stop (stop 3)");
        assertEquals("Sathyabama", etas.get(0).getStopName());
        assertTrue(etas.get(0).getDistanceKm() > 10.0);
        assertTrue(etas.get(0).getEtaMinutes() > 0);
        assertNotNull(etas.get(0).getEstimatedArrivalTime());
    }

    @Test
    void testCalculateEtaToSingleStop() {
        EtaResponseDto eta = etaCalculationService.calculateEtaToStop(stop3, 12.9010, 80.2279, 30.0);

        assertNotNull(eta);
        assertEquals(stop3.getId(), eta.getStopId());
        assertEquals("Sathyabama", eta.getStopName());
        assertTrue(eta.getDistanceKm() > 0);
        assertTrue(eta.getEtaMinutes() > 0);
    }
}
