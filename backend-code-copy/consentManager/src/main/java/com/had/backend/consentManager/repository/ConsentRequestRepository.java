package com.had.backend.consentManager.repository;

import com.had.backend.consentManager.entity.ConsentRequest;
import com.had.backend.consentManager.service.ConsentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRequestRepository extends JpaRepository<ConsentRequest, Long> {
    List<ConsentRequest> findByPatientId(String patientId);

    List<ConsentRequest> findBySourceHospitalId(String sourceHospitalId);

    List<ConsentRequest> findByPatientIdAndApproved(String patientId, boolean approved);

    Long deleteByIdCM(Long idCM);

    List<ConsentRequest>
    findSentConsentRequestByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartment(String patientId,
                                                                                      String destinationHospitalId,
                                                                                      String sourceHospitalId,
                                                                                      String department);

}
