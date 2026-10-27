package com.bustracking.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String routeNumber;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    private Double totalDistanceKm;

    private Integer estimatedDurationMinutes;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stopOrder ASC")
    private List<RouteStop> routeStops = new ArrayList<>();

    public Route() {}

    public Route(String routeNumber, String name, String origin, String destination, Double totalDistanceKm, Integer estimatedDurationMinutes, boolean active) {
        this.routeNumber = routeNumber;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.totalDistanceKm = totalDistanceKm;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public Integer getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<RouteStop> getRouteStops() {
        return routeStops;
    }

    public void setRouteStops(List<RouteStop> routeStops) {
        this.routeStops = routeStops;
    }

    public void addRouteStop(RouteStop routeStop) {
        routeStops.add(routeStop);
        routeStop.setRoute(this);
    }

    public void removeRouteStop(RouteStop routeStop) {
        routeStops.remove(routeStop);
        routeStop.setRoute(null);
    }
}
