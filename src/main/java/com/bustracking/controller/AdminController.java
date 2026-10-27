package com.bustracking.controller;

import com.bustracking.entity.*;
import com.bustracking.gps.GpsSimulatorService;
import com.bustracking.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BusService busService;
    private final RouteService routeService;
    private final DriverService driverService;
    private final TripService tripService;
    private final BusLocationService busLocationService;
    private final GpsSimulatorService gpsSimulatorService;

    public AdminController(BusService busService,
                           RouteService routeService,
                           DriverService driverService,
                           TripService tripService,
                           BusLocationService busLocationService,
                           GpsSimulatorService gpsSimulatorService) {
        this.busService = busService;
        this.routeService = routeService;
        this.driverService = driverService;
        this.tripService = tripService;
        this.busLocationService = busLocationService;
        this.gpsSimulatorService = gpsSimulatorService;
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBuses", busService.getAllBuses().size());
        model.addAttribute("totalRoutes", routeService.getAllRoutes().size());
        model.addAttribute("totalDrivers", driverService.getAllDrivers().size());
        model.addAttribute("activeTripsCount", tripService.getActiveTrips().size());
        model.addAttribute("locations", busLocationService.getAllLatestLocations());
        model.addAttribute("simulationRunning", gpsSimulatorService.isRunning());
        return "admin/dashboard";
    }

    // Bus Management
    @GetMapping("/buses")
    public String listBuses(Model model) {
        model.addAttribute("buses", busService.getAllBusDtos());
        return "admin/buses";
    }

    @GetMapping("/buses/new")
    public String newBusForm(Model model) {
        model.addAttribute("bus", new Bus());
        model.addAttribute("statuses", BusStatus.values());
        return "admin/bus-form";
    }

    @PostMapping("/buses/save")
    public String saveBus(@ModelAttribute("bus") Bus bus, RedirectAttributes ra) {
        busService.saveBus(bus);
        ra.addFlashAttribute("successMessage", "Bus '" + bus.getBusNumber() + "' saved successfully.");
        return "redirect:/admin/buses";
    }

    @GetMapping("/buses/edit/{id}")
    public String editBusForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("bus", busService.getBusById(id));
        model.addAttribute("statuses", BusStatus.values());
        return "admin/bus-form";
    }

    @PostMapping("/buses/update/{id}")
    public String updateBus(@PathVariable("id") Long id, @ModelAttribute("bus") Bus bus, RedirectAttributes ra) {
        busService.updateBus(id, bus);
        ra.addFlashAttribute("successMessage", "Bus updated successfully.");
        return "redirect:/admin/buses";
    }

    @PostMapping("/buses/delete/{id}")
    public String deleteBus(@PathVariable("id") Long id, RedirectAttributes ra) {
        busService.deleteBus(id);
        ra.addFlashAttribute("successMessage", "Bus deleted successfully.");
        return "redirect:/admin/buses";
    }

    // Route Management
    @GetMapping("/routes")
    public String listRoutes(Model model) {
        model.addAttribute("routes", routeService.getAllRouteDtos());
        return "admin/routes";
    }

    @GetMapping("/routes/new")
    public String newRouteForm(Model model) {
        model.addAttribute("route", new Route());
        return "admin/route-form";
    }

    @PostMapping("/routes/save")
    public String saveRoute(@ModelAttribute("route") Route route, RedirectAttributes ra) {
        routeService.saveRoute(route);
        ra.addFlashAttribute("successMessage", "Route '" + route.getRouteNumber() + "' saved successfully.");
        return "redirect:/admin/routes";
    }

    @GetMapping("/routes/edit/{id}")
    public String editRouteForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("route", routeService.getRouteById(id));
        return "admin/route-form";
    }

    @PostMapping("/routes/update/{id}")
    public String updateRoute(@PathVariable("id") Long id, @ModelAttribute("route") Route route, RedirectAttributes ra) {
        routeService.updateRoute(id, route);
        ra.addFlashAttribute("successMessage", "Route updated successfully.");
        return "redirect:/admin/routes";
    }

    @PostMapping("/routes/delete/{id}")
    public String deleteRoute(@PathVariable("id") Long id, RedirectAttributes ra) {
        routeService.deleteRoute(id);
        ra.addFlashAttribute("successMessage", "Route deleted successfully.");
        return "redirect:/admin/routes";
    }

    // Stops Management
    @GetMapping("/stops")
    public String listStops(Model model) {
        model.addAttribute("stops", routeService.getAllStops());
        model.addAttribute("newStop", new Stop());
        return "admin/stops";
    }

    @PostMapping("/stops/save")
    public String saveStop(@ModelAttribute("newStop") Stop stop, RedirectAttributes ra) {
        routeService.saveStop(stop);
        ra.addFlashAttribute("successMessage", "Transit stop '" + stop.getName() + "' created.");
        return "redirect:/admin/stops";
    }

    @PostMapping("/stops/delete/{id}")
    public String deleteStop(@PathVariable("id") Long id, RedirectAttributes ra) {
        routeService.deleteStop(id);
        ra.addFlashAttribute("successMessage", "Transit stop deleted.");
        return "redirect:/admin/stops";
    }

    // Driver Management
    @GetMapping("/drivers")
    public String listDrivers(Model model) {
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "admin/drivers";
    }

    @GetMapping("/drivers/new")
    public String newDriverForm(Model model) {
        model.addAttribute("driver", new Driver());
        model.addAttribute("statuses", DriverStatus.values());
        return "admin/driver-form";
    }

    @PostMapping("/drivers/save")
    public String saveDriver(@ModelAttribute("driver") Driver driver, RedirectAttributes ra) {
        driverService.saveDriver(driver);
        ra.addFlashAttribute("successMessage", "Driver '" + driver.getFullName() + "' saved.");
        return "redirect:/admin/drivers";
    }

    @GetMapping("/drivers/edit/{id}")
    public String editDriverForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("driver", driverService.getDriverById(id));
        model.addAttribute("statuses", DriverStatus.values());
        return "admin/driver-form";
    }

    @PostMapping("/drivers/update/{id}")
    public String updateDriver(@PathVariable("id") Long id, @ModelAttribute("driver") Driver driver, RedirectAttributes ra) {
        driverService.updateDriver(id, driver);
        ra.addFlashAttribute("successMessage", "Driver updated successfully.");
        return "redirect:/admin/drivers";
    }

    @PostMapping("/drivers/delete/{id}")
    public String deleteDriver(@PathVariable("id") Long id, RedirectAttributes ra) {
        driverService.deleteDriver(id);
        ra.addFlashAttribute("successMessage", "Driver deleted.");
        return "redirect:/admin/drivers";
    }

    // Trip Management
    @GetMapping("/trips")
    public String listTrips(Model model) {
        model.addAttribute("trips", tripService.getAllTripDtos());
        return "admin/trips";
    }

    @GetMapping("/trips/new")
    public String newTripForm(Model model) {
        model.addAttribute("trip", new Trip());
        model.addAttribute("buses", busService.getAllBuses());
        model.addAttribute("routes", routeService.getActiveRoutes());
        model.addAttribute("drivers", driverService.getAllDrivers());
        return "admin/trip-form";
    }

    @PostMapping("/trips/save")
    public String saveTrip(@RequestParam("tripNumber") String tripNumber,
                           @RequestParam("busId") Long busId,
                           @RequestParam("routeId") Long routeId,
                           @RequestParam(value = "driverId", required = false) Long driverId,
                           RedirectAttributes ra) {
        tripService.createTrip(tripNumber, busId, routeId, driverId);
        ra.addFlashAttribute("successMessage", "Trip schedule '" + tripNumber + "' created.");
        return "redirect:/admin/trips";
    }

    @PostMapping("/trips/start/{id}")
    public String startTrip(@PathVariable("id") Long id, RedirectAttributes ra) {
        tripService.startTrip(id);
        ra.addFlashAttribute("successMessage", "Trip dispatched and marked IN_PROGRESS.");
        return "redirect:/admin/trips";
    }

    @PostMapping("/trips/complete/{id}")
    public String completeTrip(@PathVariable("id") Long id, RedirectAttributes ra) {
        tripService.completeTrip(id);
        ra.addFlashAttribute("successMessage", "Trip marked COMPLETED.");
        return "redirect:/admin/trips";
    }

    @PostMapping("/trips/cancel/{id}")
    public String cancelTrip(@PathVariable("id") Long id, RedirectAttributes ra) {
        tripService.cancelTrip(id);
        ra.addFlashAttribute("successMessage", "Trip cancelled.");
        return "redirect:/admin/trips";
    }

    @PostMapping("/trips/delete/{id}")
    public String deleteTrip(@PathVariable("id") Long id, RedirectAttributes ra) {
        tripService.deleteTrip(id);
        ra.addFlashAttribute("successMessage", "Trip deleted.");
        return "redirect:/admin/trips";
    }

    // GPS Simulation Control
    @GetMapping("/simulation")
    public String simulationView(Model model) {
        model.addAttribute("status", gpsSimulatorService.getSimulationStatus());
        model.addAttribute("locations", busLocationService.getAllLatestLocations());
        return "admin/simulation";
    }

    @PostMapping("/simulation/start")
    public String startSimulation(RedirectAttributes ra) {
        gpsSimulatorService.startSimulation();
        ra.addFlashAttribute("successMessage", "GPS Simulation Engine started.");
        return "redirect:/admin/simulation";
    }

    @PostMapping("/simulation/stop")
    public String stopSimulation(RedirectAttributes ra) {
        gpsSimulatorService.stopSimulation();
        ra.addFlashAttribute("successMessage", "GPS Simulation Engine stopped.");
        return "redirect:/admin/simulation";
    }

    @PostMapping("/simulation/speed")
    public String updateSpeed(@RequestParam("multiplier") double multiplier, RedirectAttributes ra) {
        gpsSimulatorService.setSpeedMultiplier(multiplier);
        ra.addFlashAttribute("successMessage", "Simulation speed multiplier updated to " + multiplier + "x.");
        return "redirect:/admin/simulation";
    }
}
