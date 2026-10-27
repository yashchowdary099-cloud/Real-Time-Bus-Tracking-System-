package com.bustracking.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BusLocationDto {

    private Long busId;
    private String busNumber;
    private String registrationNumber;
    private Long tripId;
    private String tripNumber;
    private Long routeId;
    private String routeNumber;
    private String routeName;
    private double latitude;
    private double longitude;
    private double speedKmh;
    private double headingDegrees;
    private boolean isSimulated;
    private LocalDateTime timestamp;
    private String formattedTime;
    private String currentStopName;
    private String nextStopName;
    private Integer etaNextStopMinutes;
    private int currentOccupancy;
    private int capacity;
    private String crowdStatus;

    public BusLocationDto() {}

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
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

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getTripNumber() {
        return tripNumber;
    }

    public void setTripNumber(String tripNumber) {
        this.tripNumber = tripNumber;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeedKmh() {
        return speedKmh;
    }

    public void setSpeedKmh(double speedKmh) {
        this.speedKmh = speedKmh;
    }

    public double getHeadingDegrees() {
        return headingDegrees;
    }

    public void setHeadingDegrees(double headingDegrees) {
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
        if (timestamp != null) {
            this.formattedTime = timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

    public String getNextStopName() {
        return nextStopName;
    }

    public void setNextStopName(String nextStopName) {
        this.nextStopName = nextStopName;
    }

    public Integer getEtaNextStopMinutes() {
        return etaNextStopMinutes;
    }

    public void setEtaNextStopMinutes(Integer etaNextStopMinutes) {
        this.etaNextStopMinutes = etaNextStopMinutes;
    }

    public int getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(int currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getCrowdStatus() {
        return crowdStatus;
    }

    public void setCrowdStatus(String crowdStatus) {
        this.crowdStatus = crowdStatus;
    }
}
