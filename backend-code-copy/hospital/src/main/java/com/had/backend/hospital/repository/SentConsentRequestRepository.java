package com.had.backend.hospital.repository;

import com.had.backend.hospital.entity.SentConsentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentConsentRequestRepository extends JpaRepository<SentConsentRequest, Long> {
    List<SentConsentRequest> findByPatientId(String patientId);
    SentConsentRequest getById(Long Id);
    List<SentConsentRequest> findBySourceHospitalId(String sourceHospitalId);

    List<SentConsentRequest> findByPatientIdAndApproved(String patientId, boolean approved);

    List<SentConsentRequest> findByApproved(boolean approved);

    Long deleteByIdDC(Long idDC);


    List<SentConsentRequest>
    findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(String patientId,
                                                                                      boolean approved,
                                                                                      String sourceHospitalId,
                                                                                      String department);

    List<SentConsentRequest> findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartment(String patientId,
                                                                                                     String destinationHospitalId,
                                                                                                     String sourceHospitalId,
                                                                                                     String department);

    List<SentConsentRequest> findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(String patientId,
                                                                                                                String destinationHospitalId,
                                                                                                                String sourceHospitalId,
                                                                                                                String department,
                                                                                                                boolean approved);
}
