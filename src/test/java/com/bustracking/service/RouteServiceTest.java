package com.bustracking.service;

import com.bustracking.dto.RouteDto;
import com.bustracking.entity.Route;
import com.bustracking.repository.RouteRepository;
import com.bustracking.repository.RouteStopRepository;
import com.bustracking.repository.StopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;
    @Mock
    private StopRepository stopRepository;
    @Mock
    private RouteStopRepository routeStopRepository;

    @InjectMocks
    private RouteService routeService;

    private Route testRoute;

    @BeforeEach
    void setUp() {
        testRoute = new Route("ROUTE-101", "OMR Line", "Central", "Sathyabama", 28.5, 65, true);
        testRoute.setId(1L);
    }

    @Test
    void testGetActiveRoutes() {
        when(routeRepository.findByActiveTrue()).thenReturn(List.of(testRoute));

        List<Route> routes = routeService.getActiveRoutes();
        assertEquals(1, routes.size());
        assertEquals("ROUTE-101", routes.get(0).getRouteNumber());
    }

    @Test
    void testSearchRoutes() {
        when(routeRepository.searchRoutes("Sathyabama")).thenReturn(List.of(testRoute));
        when(routeStopRepository.findByRouteIdOrderByStopOrderAsc(1L)).thenReturn(Collections.emptyList());

        List<RouteDto> results = routeService.searchRoutes("Sathyabama");
        assertEquals(1, results.size());
        assertEquals("OMR Line", results.get(0).getName());
    }

    @Test
    void testSaveRoute() {
        when(routeRepository.save(any(Route.class))).thenReturn(testRoute);

        Route saved = routeService.saveRoute(testRoute);
        assertNotNull(saved);
        assertEquals("ROUTE-101", saved.getRouteNumber());
    }
}
