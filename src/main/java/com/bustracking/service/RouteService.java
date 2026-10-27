package com.bustracking.service;

import com.bustracking.dto.RouteDto;
import com.bustracking.dto.StopDto;
import com.bustracking.entity.Route;
import com.bustracking.entity.RouteStop;
import com.bustracking.entity.Stop;
import com.bustracking.exception.ResourceNotFoundException;
import com.bustracking.repository.RouteRepository;
import com.bustracking.repository.RouteStopRepository;
import com.bustracking.repository.StopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    private final RouteStopRepository routeStopRepository;

    public RouteService(RouteRepository routeRepository,
                        StopRepository stopRepository,
                        RouteStopRepository routeStopRepository) {
        this.routeRepository = routeRepository;
        this.stopRepository = stopRepository;
        this.routeStopRepository = routeStopRepository;
    }

    @Transactional(readOnly = true)
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Route> getActiveRoutes() {
        return routeRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public RouteDto getRouteDtoById(Long id) {
        Route route = getRouteById(id);
        return convertToDto(route);
    }

    @Transactional(readOnly = true)
    public List<RouteDto> getAllRouteDtos() {
        return routeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RouteDto> searchRoutes(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllRouteDtos();
        }
        return routeRepository.searchRoutes(query.trim()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    @Transactional
    public Route updateRoute(Long id, Route updated) {
        Route existing = getRouteById(id);
        existing.setRouteNumber(updated.getRouteNumber());
        existing.setName(updated.getName());
        existing.setOrigin(updated.getOrigin());
        existing.setDestination(updated.getDestination());
        existing.setTotalDistanceKm(updated.getTotalDistanceKm());
        existing.setEstimatedDurationMinutes(updated.getEstimatedDurationMinutes());
        existing.setActive(updated.isActive());
        return routeRepository.save(existing);
    }

    @Transactional
    public void deleteRoute(Long id) {
        Route route = getRouteById(id);
        routeRepository.delete(route);
    }

    // Stop operations
    @Transactional(readOnly = true)
    public List<Stop> getAllStops() {
        return stopRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Stop getStopById(Long id) {
        return stopRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stop not found with id: " + id));
    }

    @Transactional
    public Stop saveStop(Stop stop) {
        return stopRepository.save(stop);
    }

    @Transactional
    public void deleteStop(Long id) {
        Stop stop = getStopById(id);
        stopRepository.delete(stop);
    }

    @Transactional
    public void addStopToRoute(Long routeId, Long stopId, int order, Double distanceKm, Integer minutes) {
        Route route = getRouteById(routeId);
        Stop stop = getStopById(stopId);
        RouteStop rs = new RouteStop(route, stop, order, distanceKm, minutes);
        routeStopRepository.save(rs);
    }

    @Transactional
    public void removeStopFromRoute(Long routeStopId) {
        routeStopRepository.deleteById(routeStopId);
    }

    public RouteDto convertToDto(Route route) {
        RouteDto dto = new RouteDto();
        dto.setId(route.getId());
        dto.setRouteNumber(route.getRouteNumber());
        dto.setName(route.getName());
        dto.setOrigin(route.getOrigin());
        dto.setDestination(route.getDestination());
        dto.setTotalDistanceKm(route.getTotalDistanceKm());
        dto.setEstimatedDurationMinutes(route.getEstimatedDurationMinutes());
        dto.setActive(route.isActive());

        List<RouteStop> routeStops = routeStopRepository.findByRouteIdOrderByStopOrderAsc(route.getId());
        List<StopDto> stopDtos = routeStops.stream().map(rs -> {
            Stop s = rs.getStop();
            StopDto sd = new StopDto(s.getId(), s.getName(), s.getLatitude(), s.getLongitude(), s.getDescription());
            sd.setStopOrder(rs.getStopOrder());
            sd.setDistanceFromStartKm(rs.getDistanceFromStartKm());
            sd.setScheduledMinutesFromStart(rs.getScheduledMinutesFromStart());
            return sd;
        }).collect(Collectors.toList());

        dto.setStops(stopDtos);
        return dto;
    }
}
