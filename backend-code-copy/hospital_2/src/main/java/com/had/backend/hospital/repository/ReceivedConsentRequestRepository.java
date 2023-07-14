package com.had.backend.hospital.repository;

import com.had.backend.hospital.entity.ReceivedConsentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivedConsentRequestRepository extends JpaRepository<ReceivedConsentRequest, Long> {
    List<ReceivedConsentRequest> findByPatientId(String patientId);

    List<ReceivedConsentRequest> findBySourceHospitalId(String sourceHospitalId);

    List<ReceivedConsentRequest> findByPatientIdAndApproved(String patientId, boolean approved);

}
