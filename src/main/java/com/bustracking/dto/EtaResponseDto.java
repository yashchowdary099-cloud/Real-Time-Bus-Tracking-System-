package com.bustracking.dto;

public class EtaResponseDto {

    private Long stopId;
    private String stopName;
    private int stopOrder;
    private double distanceKm;
    private int etaMinutes;
    private String estimatedArrivalTime;

    public EtaResponseDto() {}

    public EtaResponseDto(Long stopId, String stopName, int stopOrder, double distanceKm, int etaMinutes, String estimatedArrivalTime) {
        this.stopId = stopId;
        this.stopName = stopName;
        this.stopOrder = stopOrder;
        this.distanceKm = distanceKm;
        this.etaMinutes = etaMinutes;
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getEtaMinutes() {
        return etaMinutes;
    }

    public void setEtaMinutes(int etaMinutes) {
        this.etaMinutes = etaMinutes;
    }

    public String getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(String estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }
}
