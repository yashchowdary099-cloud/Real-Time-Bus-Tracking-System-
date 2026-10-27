package com.bustracking.service;

import com.bustracking.entity.Driver;
import com.bustracking.entity.DriverStatus;
import com.bustracking.exception.ResourceNotFoundException;
import com.bustracking.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Driver> getDriverByUserId(Long userId) {
        return driverRepository.findByUserId(userId);
    }

    @Transactional
    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriver(Long id, Driver updated) {
        Driver existing = getDriverById(id);
        existing.setFullName(updated.getFullName());
        existing.setPhone(updated.getPhone());
        existing.setLicenseNumber(updated.getLicenseNumber());
        existing.setStatus(updated.getStatus());
        return driverRepository.save(existing);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }

    @Transactional
    public void updateStatus(Long driverId, DriverStatus status) {
        Driver driver = getDriverById(driverId);
        driver.setStatus(status);
        driverRepository.save(driver);
    }
}
