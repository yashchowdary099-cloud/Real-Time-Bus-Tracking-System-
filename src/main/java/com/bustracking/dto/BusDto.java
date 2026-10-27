package com.bustracking.dto;

import com.bustracking.entity.BusStatus;

public class BusDto {

    private Long id;
    private String busNumber;
    private String registrationNumber;
    private int capacity;
    private int currentOccupancy;
    private BusStatus status;
    private String model;
    private boolean isSimulated;
    private int availableSeats;
    private int occupancyPercentage;
    private String crowdStatus;
    private Long activeTripId;
    private String activeRouteName;

    public BusDto() {}

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
        this.currentOccupancy = currentOccupancy;
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
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(int occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public String getCrowdStatus() {
        return crowdStatus;
    }

    public void setCrowdStatus(String crowdStatus) {
        this.crowdStatus = crowdStatus;
    }

    public Long getActiveTripId() {
        return activeTripId;
    }

    public void setActiveTripId(Long activeTripId) {
        this.activeTripId = activeTripId;
    }

    public String getActiveRouteName() {
        return activeRouteName;
    }

    public void setActiveRouteName(String activeRouteName) {
        this.activeRouteName = activeRouteName;
    }
}
