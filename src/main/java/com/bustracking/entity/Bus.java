package com.bustracking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String busNumber;

    @Column(nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(nullable = false)
    private int capacity = 40;

    @Column(nullable = false)
    private int currentOccupancy = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BusStatus status = BusStatus.IN_SERVICE;

    @Column(length = 100)
    private String model;

    @Column(nullable = false)
    private boolean isSimulated = true;

    public Bus() {}

    public Bus(String busNumber, String registrationNumber, int capacity, int currentOccupancy, BusStatus status, String model, boolean isSimulated) {
        this.busNumber = busNumber;
        this.registrationNumber = registrationNumber;
        this.capacity = capacity;
        this.currentOccupancy = currentOccupancy;
        this.status = status != null ? status : BusStatus.IN_SERVICE;
        this.model = model;
        this.isSimulated = isSimulated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = Math.max(0, Math.min(currentOccupancy, capacity));
    }

    public BusStatus getStatus() {
        return status;
    }

    public void setStatus(BusStatus status) {
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean isSimulated() {
        return isSimulated;
    }

    public void setSimulated(boolean simulated) {
        isSimulated = simulated;
    }

    public int getAvailableSeats() {
        return Math.max(0, capacity - currentOccupancy);
    }

    public int getOccupancyPercentage() {
        if (capacity <= 0) return 0;
        return (int) Math.round(((double) currentOccupancy / capacity) * 100.0);
    }

    public String getCrowdStatus() {
        int pct = getOccupancyPercentage();
        if (pct >= 90) return "Crowded";
        if (pct >= 60) return "Moderate";
        return "Low Crowding";
    }
}
