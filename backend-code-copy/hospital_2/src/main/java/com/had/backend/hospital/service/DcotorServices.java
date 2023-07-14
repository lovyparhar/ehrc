package com.had.backend.hospital.service;

import com.had.backend.hospital.entity.PatientRecord;
import com.had.backend.hospital.model.AddRecordRequest;
import com.had.backend.hospital.repository.PatientRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DcotorServices {

    @Autowired
    private PatientRecordRepository patientRecordRepository;
    public Boolean addRecord(AddRecordRequest addRecordRequest) {
        try {
            var patientRecord = PatientRecord.builder()
                    .aadhar(addRecordRequest.getAadhar())
                    .address(addRecordRequest.getAddress())
                    .department(addRecordRequest.getDepartment())
                    .hospitalName(addRecordRequest.getHospitalName())
                    .diagnosis(addRecordRequest.getDiagnosis())
                    .prescription(addRecordRequest.getPrescription())
                    .build();
            patientRecordRepository.save(patientRecord);
        }
        catch (Exception exception) {
            System.out.println("exception.toString() = " + exception.toString());
            return false;
        }
        return true;
    }
}
