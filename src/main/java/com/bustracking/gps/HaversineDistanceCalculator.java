package com.bustracking.gps;

import org.springframework.stereotype.Component;

/**
 * Mathematical calculations for GPS geodetic coordinates using pure Java.
 * Implements the Great Circle Haversine formula and spherical bearing math.
 */
@Component
public class HaversineDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DEFAULT_CITY_SPEED_KMH = 30.0;

    /**
     * Calculates the great-circle distance between two GPS coordinates in kilometers.
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(rLat1) * Math.cos(rLat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates distance in meters.
     */
    public static double calculateDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistanceKm(lat1, lon1, lat2, lon2) * 1000.0;
    }

    /**
     * Calculates the forward azimuth / compass heading in degrees (0 to 360).
     */
    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double lambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(lambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) -
                   Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360.0) % 360.0;
    }

    /**
     * Linear interpolation between two coordinates given a fraction between 0.0 and 1.0.
     */
    public static Coordinate interpolate(Coordinate c1, Coordinate c2, double fraction) {
        double clamped = Math.max(0.0, Math.min(1.0, fraction));
        double lat = c1.getLatitude() + (c2.getLatitude() - c1.getLatitude()) * clamped;
        double lon = c1.getLongitude() + (c2.getLongitude() - c1.getLongitude()) * clamped;
        return new Coordinate(lat, lon);
    }

    /**
     * Estimates travel duration in minutes based on distance and speed.
     */
    public static int estimateTravelTimeMinutes(double distanceKm, double speedKmh) {
        double effectiveSpeed = speedKmh > 5.0 ? speedKmh : DEFAULT_CITY_SPEED_KMH;
        double hours = distanceKm / effectiveSpeed;
        return (int) Math.max(1, Math.round(hours * 60.0));
    }
}
