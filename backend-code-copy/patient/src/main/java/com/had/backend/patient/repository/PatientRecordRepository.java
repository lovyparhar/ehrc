package com.had.backend.patient.repository;

import com.had.backend.patient.entity.PatientRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {

    List<PatientRecord> getPatientRecordsByAadhar(String aadhar);

}

