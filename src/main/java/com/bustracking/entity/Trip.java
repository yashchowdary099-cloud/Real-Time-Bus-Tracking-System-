package com.bustracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String tripNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TripStatus status = TripStatus.SCHEDULED;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_stop_id")
    private Stop currentStop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "next_stop_id")
    private Stop nextStop;

    public Trip() {}

    public Trip(String tripNumber, Bus bus, Route route, Driver driver, TripStatus status, LocalDateTime startTime) {
        this.tripNumber = tripNumber;
        this.bus = bus;
        this.route = route;
        this.driver = driver;
        this.status = status != null ? status : TripStatus.SCHEDULED;
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Stop getCurrentStop() {
        return currentStop;
    }

    public void setCurrentStop(Stop currentStop) {
        this.currentStop = currentStop;
    }

    public Stop getNextStop() {
        return nextStop;
    }

    public void setNextStop(Stop nextStop) {
        this.nextStop = nextStop;
    }
}
