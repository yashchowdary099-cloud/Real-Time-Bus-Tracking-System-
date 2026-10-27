package com.bustracking.entity;

public enum DriverStatus {
    AVAILABLE("Available"),
    ON_TRIP("On Trip"),
    OFF_DUTY("Off Duty");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
