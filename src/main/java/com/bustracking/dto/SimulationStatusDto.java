package com.bustracking.dto;

public class SimulationStatusDto {

    private boolean running;
    private int activeBusesCount;
    private long intervalMs;
    private double speedMultiplier;
    private String statusMessage;

    public SimulationStatusDto() {}

    public SimulationStatusDto(boolean running, int activeBusesCount, long intervalMs, double speedMultiplier, String statusMessage) {
        this.running = running;
        this.activeBusesCount = activeBusesCount;
        this.intervalMs = intervalMs;
        this.speedMultiplier = speedMultiplier;
        this.statusMessage = statusMessage;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getActiveBusesCount() {
        return activeBusesCount;
    }

    public void setActiveBusesCount(int activeBusesCount) {
        this.activeBusesCount = activeBusesCount;
    }

    public long getIntervalMs() {
        return intervalMs;
    }

    public void setIntervalMs(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
