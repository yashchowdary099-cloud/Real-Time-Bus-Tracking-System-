package com.bustracking.repository;

import com.bustracking.entity.BusLocation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId ORDER BY bl.timestamp DESC")
    List<BusLocation> findRecentByBusId(@Param("busId") Long busId, Pageable pageable);

    default Optional<BusLocation> findLatestByBusId(Long busId) {
        List<BusLocation> list = findRecentByBusId(busId, org.springframework.data.domain.PageRequest.of(0, 1));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Query("SELECT bl FROM BusLocation bl WHERE bl.trip.id = :tripId ORDER BY bl.timestamp ASC")
    List<BusLocation> findByTripIdOrderByTimestampAsc(@Param("tripId") Long tripId);

    @Query("SELECT bl FROM BusLocation bl WHERE bl.timestamp >= :since ORDER BY bl.timestamp DESC")
    List<BusLocation> findAllSince(@Param("since") LocalDateTime since);

    void deleteByBusId(Long busId);
}
