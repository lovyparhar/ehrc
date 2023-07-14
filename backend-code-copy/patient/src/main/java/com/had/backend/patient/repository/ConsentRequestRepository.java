package com.had.backend.patient.repository;

import com.had.backend.patient.entity.ConsentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsentRequestRepository extends JpaRepository<ConsentRequest, Long> {
    List<ConsentRequest> findByPatientId(String patientId);

    List<ConsentRequest> findBySourceHospitalId(String sourceHospitalId);

    List<ConsentRequest> findByPatientIdAndApproved(String patientId, boolean approved);

    ConsentRequest findConsentRequestByIdPC(Long idPC);

    ConsentRequest getById(Long Id);
    Long deleteByIdPC(Long idPC);

    ConsentRequest getByIdPC(Long id);

    @Deprecated
    List<ConsentRequest> findByPatientIdAndDestinationHospitalIdAndDepartment(String patientId, String destinationHospitalId, String department);

    List<ConsentRequest> findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(String patientId, String destinationHospitalId, String sourceHospitalId, String department, Boolean approved);

    List<ConsentRequest> findSentConsentRequestByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartment(String patientId, String destinationHospitalId, String sourceHospitalId, String department);
}
