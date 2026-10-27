package com.bustracking.dto;

public class StopDto {

    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private int stopOrder;
    private Double distanceFromStartKm;
    private Integer scheduledMinutesFromStart;

    public StopDto() {}

    public StopDto(Long id, String name, double latitude, double longitude, String description) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public Double getDistanceFromStartKm() {
        return distanceFromStartKm;
    }

    public void setDistanceFromStartKm(Double distanceFromStartKm) {
        this.distanceFromStartKm = distanceFromStartKm;
    }

    public Integer getScheduledMinutesFromStart() {
        return scheduledMinutesFromStart;
    }

    public void setScheduledMinutesFromStart(Integer scheduledMinutesFromStart) {
        this.scheduledMinutesFromStart = scheduledMinutesFromStart;
    }
}
