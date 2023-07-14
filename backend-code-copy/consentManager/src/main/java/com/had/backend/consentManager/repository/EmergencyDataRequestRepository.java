package com.had.backend.consentManager.repository;

import com.had.backend.consentManager.entity.EmergencyDataRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyDataRequestRepository extends JpaRepository<EmergencyDataRequest, Long> {
}