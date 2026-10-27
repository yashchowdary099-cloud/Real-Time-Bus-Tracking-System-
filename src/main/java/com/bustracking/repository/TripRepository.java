package com.bustracking.repository;

import com.bustracking.entity.Trip;
import com.bustracking.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Optional<Trip> findByTripNumber(String tripNumber);
    List<Trip> findByStatus(TripStatus status);
    List<Trip> findByBusId(Long busId);
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByDriverId(Long driverId);

    @Query("SELECT t FROM Trip t WHERE t.bus.id = :busId AND t.status = 'IN_PROGRESS'")
    Optional<Trip> findActiveTripByBusId(@Param("busId") Long busId);

    @Query("SELECT t FROM Trip t WHERE t.driver.id = :driverId AND t.status = 'IN_PROGRESS'")
    Optional<Trip> findActiveTripByDriverId(@Param("driverId") Long driverId);

    @Query("SELECT t FROM Trip t WHERE t.status = 'IN_PROGRESS'")
    List<Trip> findAllActiveTrips();
}
