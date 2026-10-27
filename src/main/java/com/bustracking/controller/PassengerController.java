package com.bustracking.controller;

import com.bustracking.dto.BusDto;
import com.bustracking.dto.BusLocationDto;
import com.bustracking.dto.EtaResponseDto;
import com.bustracking.dto.RouteDto;
import com.bustracking.entity.Bus;
import com.bustracking.entity.Route;
import com.bustracking.entity.Trip;
import com.bustracking.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class PassengerController {

    private final BusService busService;
    private final RouteService routeService;
    private final TripService tripService;
    private final BusLocationService busLocationService;
    private final EtaCalculationService etaCalculationService;

    public PassengerController(BusService busService,
                               RouteService routeService,
                               TripService tripService,
                               BusLocationService busLocationService,
                               EtaCalculationService etaCalculationService) {
        this.busService = busService;
        this.routeService = routeService;
        this.tripService = tripService;
        this.busLocationService = busLocationService;
        this.etaCalculationService = etaCalculationService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("activeBuses", busService.getAllBusDtos());
        model.addAttribute("activeRoutes", routeService.getActiveRoutes());
        model.addAttribute("activeTrips", tripService.getActiveTrips());
        return "passenger/home";
    }

    @GetMapping("/track")
    public String trackAll(Model model) {
        List<BusDto> buses = busService.getAllBusDtos();
        List<RouteDto> routes = routeService.getAllRouteDtos();
        List<BusLocationDto> locations = busLocationService.getAllLatestLocations();

        model.addAttribute("buses", buses);
        model.addAttribute("routes", routes);
        model.addAttribute("locations", locations);
        model.addAttribute("selectedBusId", null);
        return "passenger/track";
    }

    @GetMapping("/track/{busId}")
    public String trackSpecificBus(@PathVariable("busId") Long busId, Model model) {
        List<BusDto> buses = busService.getAllBusDtos();
        List<RouteDto> routes = routeService.getAllRouteDtos();
        List<BusLocationDto> locations = busLocationService.getAllLatestLocations();

        Bus selectedBus = busService.getBusById(busId);
        Optional<BusLocationDto> selectedLocation = busLocationService.getLatestLocation(busId);
        Optional<Trip> activeTrip = tripService.getActiveTripByBusId(busId);

        List<EtaResponseDto> etas = null;
        if (activeTrip.isPresent() && selectedLocation.isPresent()) {
            BusLocationDto loc = selectedLocation.get();
            Trip trip = activeTrip.get();
            Route route = trip.getRoute();
            int currentStopOrder = (trip.getCurrentStop() != null) ? 1 : 0;
            etas = etaCalculationService.calculateEtasForRemainingStops(
                    route, loc.getLatitude(), loc.getLongitude(), loc.getSpeedKmh(), currentStopOrder
            );
        }

        model.addAttribute("buses", buses);
        model.addAttribute("routes", routes);
        model.addAttribute("locations", locations);
        model.addAttribute("selectedBus", selectedBus);
        model.addAttribute("selectedLocation", selectedLocation.orElse(null));
        model.addAttribute("selectedBusId", busId);
        model.addAttribute("activeTrip", activeTrip.orElse(null));
        model.addAttribute("etas", etas);
        return "passenger/track";
    }

    @GetMapping("/routes")
    public String viewRoutes(Model model) {
        model.addAttribute("routes", routeService.getAllRouteDtos());
        return "passenger/routes";
    }

    @GetMapping("/routes/{id}")
    public String viewRouteDetail(@PathVariable("id") Long id, Model model) {
        RouteDto route = routeService.getRouteDtoById(id);
        List<Trip> activeTrips = tripService.getActiveTrips();
        model.addAttribute("route", route);
        model.addAttribute("activeTrips", activeTrips);
        return "passenger/route-detail";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("query", query);
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("routes", routeService.searchRoutes(query));
        } else {
            model.addAttribute("routes", routeService.getAllRouteDtos());
        }
        return "passenger/search";
    }
}
