package com.bustracking.controller;

import com.bustracking.entity.Driver;
import com.bustracking.entity.Trip;
import com.bustracking.security.CustomUserDetails;
import com.bustracking.service.BusLocationService;
import com.bustracking.service.DriverService;
import com.bustracking.service.TripService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/driver")
public class DriverController {

    private final DriverService driverService;
    private final TripService tripService;
    private final BusLocationService busLocationService;

    public DriverController(DriverService driverService,
                            TripService tripService,
                            BusLocationService busLocationService) {
        this.driverService = driverService;
        this.tripService = tripService;
        this.busLocationService = busLocationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Driver driver = null;
        if (userDetails != null) {
            Optional<Driver> driverOpt = driverService.getDriverByUserId(userDetails.getId());
            driver = driverOpt.orElse(null);
        }

        // Fallback to first driver if not linked directly
        if (driver == null && !driverService.getAllDrivers().isEmpty()) {
            driver = driverService.getAllDrivers().get(0);
        }

        Trip activeTrip = null;
        if (driver != null) {
            activeTrip = tripService.getActiveTripByDriverId(driver.getId()).orElse(null);
        }

        model.addAttribute("driver", driver);
        model.addAttribute("activeTrip", activeTrip);
        return "driver/dashboard";
    }

    @PostMapping("/trip/start/{id}")
    public String startTrip(@PathVariable("id") Long id, RedirectAttributes ra) {
        tripService.startTrip(id);
        ra.addFlashAttribute("successMessage", "Trip started! GPS coordinates are being transmitted.");
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/trip/complete/{id}")
    public String completeTrip(@PathVariable("id") Long id, RedirectAttributes ra) {
        tripService.completeTrip(id);
        ra.addFlashAttribute("successMessage", "Trip finished successfully. Thank you for your service!");
        return "redirect:/driver/dashboard";
    }

    @PostMapping("/location/beacon")
    public String sendManualBeacon(@RequestParam("busId") Long busId,
                                   @RequestParam("latitude") double latitude,
                                   @RequestParam("longitude") double longitude,
                                   @RequestParam(value = "speedKmh", defaultValue = "35.0") double speedKmh,
                                   RedirectAttributes ra) {
        busLocationService.updateBusLocation(busId, latitude, longitude, speedKmh, 0.0, false);
        ra.addFlashAttribute("successMessage", "GPS beacon transmitted manually: (" + latitude + ", " + longitude + ")");
        return "redirect:/driver/dashboard";
    }
}
