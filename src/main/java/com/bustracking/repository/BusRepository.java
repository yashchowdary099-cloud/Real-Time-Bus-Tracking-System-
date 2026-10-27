package com.bustracking.repository;

import com.bustracking.entity.Bus;
import com.bustracking.entity.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByBusNumber(String busNumber);
    Optional<Bus> findByRegistrationNumber(String registrationNumber);
    List<Bus> findByStatus(BusStatus status);
    List<Bus> findByIsSimulatedTrue();
    boolean existsByBusNumber(String busNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
}
