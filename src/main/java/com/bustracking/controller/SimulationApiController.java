package com.bustracking.controller;

import com.bustracking.dto.BusLocationDto;
import com.bustracking.dto.EtaResponseDto;
import com.bustracking.dto.RouteDto;
import com.bustracking.dto.SimulationStatusDto;
import com.bustracking.entity.Route;
import com.bustracking.entity.Trip;
import com.bustracking.gps.GpsSimulatorService;
import com.bustracking.service.BusLocationService;
import com.bustracking.service.EtaCalculationService;
import com.bustracking.service.RouteService;
import com.bustracking.service.TripService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SimulationApiController {

    private final BusLocationService busLocationService;
    private final RouteService routeService;
    private final TripService tripService;
    private final EtaCalculationService etaCalculationService;
    private final GpsSimulatorService gpsSimulatorService;

    public SimulationApiController(BusLocationService busLocationService,
                                   RouteService routeService,
                                   TripService tripService,
                                   EtaCalculationService etaCalculationService,
                                   GpsSimulatorService gpsSimulatorService) {
        this.busLocationService = busLocationService;
        this.routeService = routeService;
        this.tripService = tripService;
        this.etaCalculationService = etaCalculationService;
        this.gpsSimulatorService = gpsSimulatorService;
    }

    @GetMapping("/locations")
    public ResponseEntity<List<BusLocationDto>> getAllLocations() {
        return ResponseEntity.ok(busLocationService.getAllLatestLocations());
    }

    @GetMapping("/locations/{busId}")
    public ResponseEntity<BusLocationDto> getLocationForBus(@PathVariable("busId") Long busId) {
        return busLocationService.getLatestLocation(busId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/routes/{routeId}")
    public ResponseEntity<RouteDto> getRouteDetails(@PathVariable("routeId") Long routeId) {
        return ResponseEntity.ok(routeService.getRouteDtoById(routeId));
    }

    @GetMapping("/eta/{busId}")
    public ResponseEntity<List<EtaResponseDto>> getEtasForBus(@PathVariable("busId") Long busId) {
        Optional<Trip> tripOpt = tripService.getActiveTripByBusId(busId);
        Optional<BusLocationDto> locOpt = busLocationService.getLatestLocation(busId);

        if (tripOpt.isPresent() && locOpt.isPresent()) {
            BusLocationDto loc = locOpt.get();
            Trip trip = tripOpt.get();
            Route route = trip.getRoute();
            int currentStopOrder = (trip.getCurrentStop() != null) ? 1 : 0;
            List<EtaResponseDto> etas = etaCalculationService.calculateEtasForRemainingStops(
                    route, loc.getLatitude(), loc.getLongitude(), loc.getSpeedKmh(), currentStopOrder
            );
            return ResponseEntity.ok(etas);
        }

        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/simulation/status")
    public ResponseEntity<SimulationStatusDto> getSimulationStatus() {
        return ResponseEntity.ok(gpsSimulatorService.getSimulationStatus());
    }

    @PostMapping("/simulation/start")
    public ResponseEntity<SimulationStatusDto> startSimulation() {
        gpsSimulatorService.startSimulation();
        return ResponseEntity.ok(gpsSimulatorService.getSimulationStatus());
    }

    @PostMapping("/simulation/stop")
    public ResponseEntity<SimulationStatusDto> stopSimulation() {
        gpsSimulatorService.stopSimulation();
        return ResponseEntity.ok(gpsSimulatorService.getSimulationStatus());
    }

    @PostMapping("/driver/location")
    public ResponseEntity<BusLocationDto> receiveDriverBeacon(@RequestParam("busId") Long busId,
                                                              @RequestParam("latitude") double latitude,
                                                              @RequestParam("longitude") double longitude,
                                                              @RequestParam(value = "speedKmh", defaultValue = "35.0") double speedKmh,
                                                              @RequestParam(value = "headingDegrees", defaultValue = "0.0") double headingDegrees) {
        BusLocationDto updated = busLocationService.updateBusLocation(busId, latitude, longitude, speedKmh, headingDegrees, false);
        return ResponseEntity.ok(updated);
    }
}
