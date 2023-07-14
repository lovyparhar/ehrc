package com.had.backend.hospital.repository;

import com.had.backend.hospital.entity.EmergencyDataRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyDataRequestRepository extends JpaRepository<EmergencyDataRequest, Long> {
}