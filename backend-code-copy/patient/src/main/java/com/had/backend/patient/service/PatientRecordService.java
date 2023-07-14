package com.had.backend.patient.service;

import com.had.backend.patient.entity.PatientRecord;
import com.had.backend.patient.repository.PatientRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientRecordService {
    @Autowired
    private PatientRecordRepository patientRecordRepository;

    //Strictly for Testing purposes
    public Boolean insertRecord() {
        var patientRecord = PatientRecord.builder()
                .aadhar("888888")
                .address("Bangalore")
                .department("Oncology")
                .hospitalName("H1")
                .diagnosis("Cancer")
                .prescription("Chemotherapy")
                .build();
        System.out.println(patientRecord.toString());
        patientRecordRepository.save(patientRecord);
        System.out.println("Records Saved!!!!");

        return false;
    }
}
