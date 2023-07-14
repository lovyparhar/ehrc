package com.had.backend.patient.messageConsumer;

import com.had.backend.patient.config.MessagingConfig;
import com.had.backend.patient.entity.PatientRecord;
import com.had.backend.patient.model.DataMessage;
import com.had.backend.patient.model.DataMessageEncrypted;
import com.had.backend.patient.repository.PatientRecordRepository;
import com.had.backend.patient.model.GenericMessage;
import com.had.backend.patient.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataMessageConsumer {

    @Autowired
    private PatientRecordRepository patientRecordRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMessageConsumer.class);
    @Autowired
    private DataService dataService;

    @RabbitListener(queues = {MessagingConfig.RECEIVE_DATA_QUEUE})
//    public void consumeDataMessage(DataMessageEncrypted dataMessageEncrypted){
//        DataMessage dataMessage = dataMessageEncrypted.decryptRecord();
//
//        LOGGER.info(String.format("Received Data message -> %s", dataMessage.toString()));
//
//        // Send the data message to front end
////        dataService.sendDataToFrontEnd(dataMessage);
//
//        System.out.println(dataMessage.toString());
//        var patientRecord = PatientRecord.builder()
//                .aadhar(dataMessage.getAadhar())
//                .address(dataMessage.getAddress())
//                .department(dataMessage.getDepartment())
//                .hospitalName(dataMessage.getSourceId())
//                .diagnosis(dataMessage.getDiagnosis())
//                .prescription(dataMessage.getPrescription())
//                .doctorId(dataMessage.getDoctorId())
//                .patientFirstName(dataMessage.getPatientFirstName())
//                .patientLastName(dataMessage.getPatientLastName())
//                .build();
//        System.out.println(patientRecord.toString());
//        patientRecordRepository.save(patientRecord);
//        System.out.println("Records Saved!!!!");
//    }

    @RabbitListener(queues = {MessagingConfig.RECEIVE_DATA_QUEUE})
    public void consumeDataMessage(DataMessage dataMessage){
//        DataMessage dataMessage = dataMessageEncrypted.decryptRecord();

        LOGGER.info(String.format("Received Data message -> %s", dataMessage.toString()));

        // Send the data message to front end
//        dataService.sendDataToFrontEnd(dataMessage);

        var patientRecord = PatientRecord.builder()
                .aadhar(dataMessage.getAadhar())
                .address(dataMessage.getAddress())
                .department(dataMessage.getDepartment())
                .hospitalName(dataMessage.getSourceId())
                .diagnosis(dataMessage.getDiagnosis())
                .prescription(dataMessage.getPrescription())
                .doctorId(dataMessage.getDoctorId())
                .patientFirstName(dataMessage.getPatientFirstName())
                .patientLastName(dataMessage.getPatientLastName())
                .build();

        System.out.println(patientRecord.toString());
        patientRecordRepository.save(patientRecord);
        System.out.println("Records Saved!!!!");
    }
}
