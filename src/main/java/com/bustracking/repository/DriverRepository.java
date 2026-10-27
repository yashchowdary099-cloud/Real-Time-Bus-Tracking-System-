package com.bustracking.repository;

import com.bustracking.entity.Driver;
import com.bustracking.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByLicenseNumber(String licenseNumber);
    Optional<Driver> findByUserId(Long userId);
    List<Driver> findByStatus(DriverStatus status);
}
