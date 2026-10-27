package com.bustracking.repository;

import com.bustracking.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByRouteNumber(String routeNumber);
    List<Route> findByActiveTrue();
    boolean existsByRouteNumber(String routeNumber);

    @Query("SELECT r FROM Route r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.origin) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.destination) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Route> searchRoutes(@Param("query") String query);
}
