package com.had.backend.hospital.service;

import com.had.backend.hospital.entity.PatientRecord;
import com.had.backend.hospital.entity.ReceivedRecords;
import com.had.backend.hospital.entity.User;
import com.had.backend.hospital.model.AddRecordRequest;
import com.had.backend.hospital.model.DataMessage;
import com.had.backend.hospital.repository.PatientRecordRepository;
import com.had.backend.hospital.repository.ReceivedRecordsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RecordServices {

    @Autowired
    private PatientRecordRepository patientRecordRepository;

    @Autowired
    private ReceivedRecordsRepository receivedRecordsRepository;

    public Boolean doctorAddRecord(AddRecordRequest addRecordRequest) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            PatientRecord patientRecord = PatientRecord.builder()
                    .aadhar(addRecordRequest.getAadhar())
                    .address(addRecordRequest.getAddress())
                    .department(addRecordRequest.getDepartment())
                    .hospitalName(addRecordRequest.getHospitalName())
                    .doctorId(user.getAadhar())
                    .diagnosis(addRecordRequest.getDiagnosis())
                    .prescription(addRecordRequest.getPrescription())
                    .patientLastName(addRecordRequest.getPatientLastName())
                    .patientFirstName(addRecordRequest.getPatientFirstName())
                    .build();
            System.out.println("patientRecord = " + patientRecord);
            patientRecordRepository.save(patientRecord);
        }
        catch (Exception exception) {
            System.out.println("exception.toString() = " + exception.toString());
            return false;
        }
        return true;
    }

    public Boolean doctorUpdateRecord(AddRecordRequest addRecordRequest) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            var patientRecord = patientRecordRepository.getPatientRecordByAadharAndDepartmentAndDoctorIdAndPrescriptionIsNullAndDiagnosisIsNull(addRecordRequest.getAadhar(), addRecordRequest.getDepartment(),  user.getAadhar());
            System.out.println("patientRecord = " + patientRecord);
            patientRecord.setDiagnosis(addRecordRequest.getDiagnosis());
            patientRecord.setPrescription(addRecordRequest.getPrescription());
            patientRecordRepository.save(patientRecord);
        }
        catch (Exception exception) {
            System.out.println("exception.toString() = " + exception.toString());
            return false;
        }
        return true;
    }

    public Boolean  staffAddRecord(AddRecordRequest addRecordRequest) {
        try {
            var patientRecord = patientRecordRepository.getPatientRecordByAadharAndDepartmentAndDoctorIdAndPrescriptionIsNullAndDiagnosisIsNull(addRecordRequest.getAadhar(), addRecordRequest.getDepartment(),  addRecordRequest.getDoctorId());
            if(patientRecord != null) {
                System.out.println("Partial Record Already Exists!!");
                return true;
            }
            patientRecord = PatientRecord.builder()
                    .aadhar(addRecordRequest.getAadhar())
                    .address(addRecordRequest.getAddress())
                    .department(addRecordRequest.getDepartment())
                    .hospitalName(addRecordRequest.getHospitalName())
                    .doctorId(addRecordRequest.getDoctorId())
                    .patientFirstName(addRecordRequest.getPatientFirstName())
                    .patientLastName(addRecordRequest.getPatientLastName())
                    .build();
            System.out.println("patientRecord = " + patientRecord);
            patientRecordRepository.save(patientRecord);
        }
        catch (Exception exception) {
            System.out.println("exception.toString() = " + exception.toString());
            return false;
        }
        return true;
    }

    public void addReceivedRecord(DataMessage dataMessage) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User user = (User) auth.getPrincipal();

        ReceivedRecords rc = ReceivedRecords.builder()
                .hospitalName(dataMessage.getSourceId())
                .aadhar(dataMessage.getAadhar())
                .prescription(dataMessage.getPrescription())
                .diagnosis(dataMessage.getDiagnosis())
                .department(dataMessage.getDepartment())
                .address(dataMessage.getAddress())
                .doctorId(dataMessage.getDoctorId())
                .patientFirstName(dataMessage.getPatientFirstName())
                .patientLastName(dataMessage.getPatientLastName())
                .recievedDate(LocalDateTime.now())
                .patientLastName(dataMessage.getPatientLastName())
                .patientFirstName(dataMessage.getPatientFirstName())
                .build();

        System.out.println("rc = " + rc);

        receivedRecordsRepository.save(rc);
    }

    public List<ReceivedRecords> getReceivedRecords() {
        // checks and validation of consent before getting the records.
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        System.out.println("user = " + user);
        List<ReceivedRecords> receivedRecords = receivedRecordsRepository.getReceivedRecordsByDoctorId(user.getAadhar());


        List<ReceivedRecords> toDelete = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for(ReceivedRecords record: receivedRecords) {
            LocalDateTime recvDate = record.getRecievedDate();
            if(recvDate.plusMinutes(30).isBefore(now)) {
                toDelete.add(record);
            }
        }

        System.out.println("Records toDelete = " + toDelete.size());
        receivedRecordsRepository.deleteAllInBatch(toDelete);
        return  receivedRecordsRepository.getReceivedRecordsByDoctorId(user.getAadhar());
    }

    public List<PatientRecord> getPatientRecords() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return patientRecordRepository.getPatientRecordsByDoctorIdAndPrescriptionIsNotNullAndDiagnosisIsNotNull(user.getAadhar());
    }

    public void clearRecords() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        receivedRecordsRepository.deleteAllByDoctorId(user.getAadhar());
    }

    public List<PatientRecord> getPendingRecords() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        return patientRecordRepository.getPatientRecordsByDoctorIdAndPrescriptionIsNullAndDiagnosisIsNull(user.getAadhar());
    }

}
