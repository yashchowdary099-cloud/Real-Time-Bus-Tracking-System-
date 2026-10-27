package com.bustracking.gps;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HaversineDistanceCalculatorTest {

    @Test
    void testCalculateDistanceKmBetweenKnownCoordinates() {
        // Chennai Central Station (13.0827, 80.2707) to Guindy (13.0067, 80.2030)
        double distance = HaversineDistanceCalculator.calculateDistanceKm(13.0827, 80.2707, 13.0067, 80.2030);

        // Distance is approx 11.1 km
        assertTrue(distance > 10.0 && distance < 13.0, "Distance should be approx 11 km, was: " + distance);
    }

    @Test
    void testZeroDistanceForIdenticalCoordinates() {
        double distance = HaversineDistanceCalculator.calculateDistanceKm(12.8718, 80.2206, 12.8718, 80.2206);
        assertEquals(0.0, distance, 0.0001);
    }

    @Test
    void testCalculateBearing() {
        // Due North: from (12.0, 80.0) to (13.0, 80.0)
        double bearing = HaversineDistanceCalculator.calculateBearing(12.0, 80.0, 13.0, 80.0);
        assertEquals(0.0, bearing, 0.5);

        // Due East: from (12.0, 80.0) to (12.0, 81.0)
        bearing = HaversineDistanceCalculator.calculateBearing(12.0, 80.0, 12.0, 81.0);
        assertEquals(90.0, bearing, 1.0);
    }

    @Test
    void testInterpolateMidpoint() {
        Coordinate c1 = new Coordinate(10.0, 20.0);
        Coordinate c2 = new Coordinate(20.0, 40.0);

        Coordinate mid = HaversineDistanceCalculator.interpolate(c1, c2, 0.5);
        assertEquals(15.0, mid.getLatitude(), 0.0001);
        assertEquals(30.0, mid.getLongitude(), 0.0001);
    }

    @Test
    void testEstimateTravelTimeMinutes() {
        // 30 km at 30 km/h should take 60 minutes
        int minutes = HaversineDistanceCalculator.estimateTravelTimeMinutes(30.0, 30.0);
        assertEquals(60, minutes);

        // 15 km at 30 km/h should take 30 minutes
        minutes = HaversineDistanceCalculator.estimateTravelTimeMinutes(15.0, 30.0);
        assertEquals(30, minutes);
    }
}
