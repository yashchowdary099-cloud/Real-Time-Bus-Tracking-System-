package com.bustracking.entity;

public enum BusStatus {
    IN_SERVICE("In Service"),
    MAINTENANCE("Under Maintenance"),
    OUT_OF_SERVICE("Out of Service");

    private final String displayName;

    BusStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
