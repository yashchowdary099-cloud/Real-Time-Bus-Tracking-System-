package com.bustracking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bus_locations", indexes = {
    @Index(name = "idx_bus_loc_bus_time", columnList = "bus_id, timestamp")
})
public class BusLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double speedKmh = 0.0;

    @Column(nullable = false)
    private Double headingDegrees = 0.0;

    @Column(nullable = false)
    private boolean isSimulated = true;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public BusLocation() {}

    public BusLocation(Bus bus, Trip trip, Double latitude, Double longitude, Double speedKmh, Double headingDegrees, boolean isSimulated, LocalDateTime timestamp) {
        this.bus = bus;
        this.trip = trip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKmh = speedKmh != null ? speedKmh : 0.0;
        this.headingDegrees = headingDegrees != null ? headingDegrees : 0.0;
        this.isSimulated = isSimulated;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getSpeedKmh() {
        return speedKmh;
    }

    public void setSpeedKmh(Double speedKmh) {
        this.speedKmh = speedKmh;
    }

    public Double getHeadingDegrees() {
        return headingDegrees;
    }

    public void setHeadingDegrees(Double headingDegrees) {
        this.headingDegrees = headingDegrees;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
