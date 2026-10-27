package com.bustracking.service;

import com.bustracking.dto.EtaResponseDto;
import com.bustracking.entity.Route;
import com.bustracking.entity.RouteStop;
import com.bustracking.entity.Stop;
import com.bustracking.gps.HaversineDistanceCalculator;
import com.bustracking.repository.RouteStopRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EtaCalculationService {

    private final RouteStopRepository routeStopRepository;

    public EtaCalculationService(RouteStopRepository routeStopRepository) {
        this.routeStopRepository = routeStopRepository;
    }

    /**
     * Calculates ETAs from current bus position to all remaining stops along the route.
     *
     * @param route The active transit route.
     * @param currentLat Current bus latitude.
     * @param currentLon Current bus longitude.
     * @param currentSpeedKmh Current speed in km/h.
     * @param currentStopOrder The last visited stop order (1-indexed).
     * @return List of ETAs for subsequent stops.
     */
    public List<EtaResponseDto> calculateEtasForRemainingStops(Route route,
                                                              double currentLat,
                                                              double currentLon,
                                                              double currentSpeedKmh,
                                                              int currentStopOrder) {
        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopOrderAsc(route.getId());
        List<EtaResponseDto> results = new ArrayList<>();

        double cumulativeDistanceKm = 0.0;
        double lastLat = currentLat;
        double lastLon = currentLon;
        LocalTime now = LocalTime.now();

        // Effective speed: at least 25 km/h for city bus prediction to prevent infinite ETA when stopped at a signal
        double speed = currentSpeedKmh > 10.0 ? currentSpeedKmh : 30.0;

        for (RouteStop rs : routeStops) {
            if (rs.getStopOrder() > currentStopOrder) {
                Stop stop = rs.getStop();
                double legDistance = HaversineDistanceCalculator.calculateDistanceKm(
                        lastLat, lastLon, stop.getLatitude(), stop.getLongitude()
                );
                cumulativeDistanceKm += legDistance;

                int minutes = HaversineDistanceCalculator.estimateTravelTimeMinutes(cumulativeDistanceKm, speed);
                LocalTime etaTime = now.plusMinutes(minutes);

                results.add(new EtaResponseDto(
                        stop.getId(),
                        stop.getName(),
                        rs.getStopOrder(),
                        Math.round(cumulativeDistanceKm * 10.0) / 10.0,
                        minutes,
                        etaTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
                ));

                lastLat = stop.getLatitude();
                lastLon = stop.getLongitude();
            }
        }

        return results;
    }

    /**
     * Calculates distance and ETA to a single target stop.
     */
    public EtaResponseDto calculateEtaToStop(Stop stop, double currentLat, double currentLon, double currentSpeedKmh) {
        double distanceKm = HaversineDistanceCalculator.calculateDistanceKm(
                currentLat, currentLon, stop.getLatitude(), stop.getLongitude()
        );
        double speed = currentSpeedKmh > 10.0 ? currentSpeedKmh : 30.0;
        int minutes = HaversineDistanceCalculator.estimateTravelTimeMinutes(distanceKm, speed);
        LocalTime etaTime = LocalTime.now().plusMinutes(minutes);

        return new EtaResponseDto(
                stop.getId(),
                stop.getName(),
                0,
                Math.round(distanceKm * 10.0) / 10.0,
                minutes,
                etaTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        );
    }
}
