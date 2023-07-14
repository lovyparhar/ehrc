package com.had.backend.hospital.repository;

import com.had.backend.hospital.entity.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {

    List<PatientRecord> getPatientRecordsByAadhar(String aadhar);

    List<PatientRecord> getPatientRecordsByAadharAndDepartment(String aadhar, String department);

}
