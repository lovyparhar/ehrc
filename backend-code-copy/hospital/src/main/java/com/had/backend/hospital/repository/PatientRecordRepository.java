package com.had.backend.hospital.repository;

import com.had.backend.hospital.entity.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {

    List<PatientRecord> getPatientRecordsByAadharAndDepartmentAndPrescriptionIsNotNullAndDiagnosisIsNotNull(String aadhar, String department);

    List<PatientRecord> getPatientRecordsByDoctorIdAndPrescriptionIsNotNullAndDiagnosisIsNotNull(String doctorId);

    List<PatientRecord> getPatientRecordsByDoctorIdAndPrescriptionIsNullAndDiagnosisIsNull(String doctorId);

    PatientRecord getPatientRecordByAadharAndDepartmentAndDoctorIdAndPrescriptionIsNullAndDiagnosisIsNull(String aadhar, String department, String doctorId);

}
