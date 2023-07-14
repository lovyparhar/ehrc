package com.had.backend.patient.service;

import com.had.backend.patient.entity.PatientRecord;
import com.had.backend.patient.repository.PatientRecordRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class PatientRecordTest {
    @Autowired
    private PatientRecordRepository patientRecordRepository;

    public void insertBatchData() {
        var pr1 = PatientRecord.builder()
                .aadhar("888888")
                .address("Bangalore")
                .department("Oncology")
                .hospitalName("H1")
                .diagnosis("Cancer")
                .prescription("Chemotherapy")
                .build();

        var pr2 = PatientRecord.builder()
                .aadhar("111111")
                .address("Jalandhar")
                .department("Radiology")
                .hospitalName("H2")
                .diagnosis("Knee Injury")
                .prescription("Physio")
                .build();


        var pr3 = PatientRecord.builder()
                .aadhar("666666")
                .address("Chandigarh")
                .department("Urology")
                .hospitalName("H3")
                .diagnosis("Kidney Failure")
                .prescription("Electrolytes")
                .build();

        List<PatientRecord> records = new ArrayList<>();
        records.add(pr1);
        records.add(pr2);
        records.add(pr3);
        patientRecordRepository.saveAll(records);
    }


    @Test
    public void addRecords() {
        insertBatchData();
        List<PatientRecord> records = patientRecordRepository.getPatientRecordsByAadhar("888888");
        System.out.println("records = " + records);
    }
}
