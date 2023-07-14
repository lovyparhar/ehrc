package com.had.backend.hospital.service;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.entity.*;
import com.had.backend.hospital.model.ConsentMessage;
import com.had.backend.hospital.model.DataMessage;
import com.had.backend.hospital.model.RequestConsentObject;
import com.had.backend.hospital.model.RequestDataObject;
import com.had.backend.hospital.repository.PatientRecordRepository;
import com.had.backend.hospital.repository.ReceivedConsentRequestRepository;
import com.had.backend.hospital.repository.ReceivedRecordsRepository;
import com.had.backend.hospital.repository.SentConsentRequestRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsentService {
    public static final String CM_EXCHANGE= "C_exchange";
    public static final String CM_HOSPITAL_ROUTING_KEY = "C_hospitalRoutingKey";
    public static final String CM_DATA_REQUEST_ROUTING_KEY = "C_dataRequestRoutingKey";
    public static final String DM_EXCHANGE= "D_exchange";
    public static final String DM_PATIENT_ROUTING_KEY = "D_dataRoutingKey";
    public static final String CM_DATA_ROUTING_KEY = "C_dataRoutingKey";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ReceivedConsentRequestRepository receivedConsentRequestRepository;

    @Autowired
    private SentConsentRequestRepository sentConsentRequestRepository;

    @Autowired
    private PatientRecordRepository patientRecordRepository;

    @Autowired
    private ReceivedRecordsRepository receivedRecordsRepository;

    public Boolean requestConsent(RequestConsentObject requestConsentObject) {

        String recordSenderHospital = requestConsentObject.getRecordSenderHospital();
        String recordRequesterHospital = requestConsentObject.getRecordRequesterHospital();
        String patientId = requestConsentObject.getPatientId();
        LocalDateTime endTime = requestConsentObject.getEndTime();
        String department = requestConsentObject.getDepartment();

        // fetch all consents for this patient with the destination hospital that are active now.
        List<SentConsentRequest> sentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(patientId,
                                true,
                                recordSenderHospital,
                                department);

        if(sentConsentRequests.size() == 0) {
            // TODO: found no consents, consent has to go to patient for approval

            SentConsentRequest sentConsentRequest = SentConsentRequest.builder()
                    .approved(false)
                    .patientId(patientId)
                    .sourceHospitalId(recordSenderHospital)
                    .destinationHospitalId(recordRequesterHospital)
                    .endTime(endTime)
                    .startTime(LocalDateTime.now())
                    .department(department)
                    .build();

            sentConsentRequest = sentConsentRequestRepository.saveAndFlush(sentConsentRequest);
            ConsentMessage toSend = ConsentMessage.builder()
                    .department(sentConsentRequest.getDepartment())
                    .idPC(sentConsentRequest.getIdPC())
                    .idDC(sentConsentRequest.getIdDC())
                    .idSC(sentConsentRequest.getIdSC())
                    .approved(sentConsentRequest.isApproved())
                    .patientId(sentConsentRequest.getPatientId())
                    .sourceHospitalId(sentConsentRequest.getSourceHospitalId())
                    .destinationHospitalId(sentConsentRequest.getDestinationHospitalId())
                    .startTime(sentConsentRequest.getStartTime())
                    .endTime(sentConsentRequest.getEndTime())
                    .build();

            // If it passes all checks, send the consent request message to the consent manager
            rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_HOSPITAL_ROUTING_KEY, toSend);
            System.out.println("Consent Request routed to CM for patient approval: " + toSend.toString());
            return false;
        }
        else {
            // only one consent object found -> sentConsentRequests.size() == 1
            // update this consent object, with the end time to be the max of both times
            if(sentConsentRequests.get(0).getEndTime().isBefore(endTime)) {
                Long sentConsentRequestId = sentConsentRequests.get(0).getId();
                SentConsentRequest sentConsentRequest = sentConsentRequestRepository.getById(sentConsentRequestId);
                sentConsentRequest.setEndTime(endTime);
                sentConsentRequestRepository.saveAndFlush(sentConsentRequest);
            }

            SentConsentRequest sentConsentRequestFinal = sentConsentRequests.get(0);
            rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_HOSPITAL_ROUTING_KEY, sentConsentRequestFinal);
            return true;
        }
    }

    public Boolean updateConsent(ConsentMessage consentMessage) {
        String destinationHospital = consentMessage.getDestinationHospitalId();
        String patientId = consentMessage.getPatientId();
        LocalDateTime endTime = consentMessage.getEndTime();
        String department = consentMessage.getDepartment();

        // fetch all consents for this patient with the destination hospital that are active now.
        List<SentConsentRequest> sentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndSourceHospitalIdAndDepartment(patientId,
                                destinationHospital,
                                department);

        for(SentConsentRequest s : sentConsentRequests) {
            System.out.println("s = " + s);
        }

        // If already
        if(sentConsentRequests.size() != 0) {

            SentConsentRequest toUpdate = sentConsentRequests.get(0);
            toUpdate.setApproved(consentMessage.isApproved());
            toUpdate.setEndTime(consentMessage.getEndTime());
            sentConsentRequestRepository.save(toUpdate);
        }
        else {
//            SentConsentRequest newConsent = sentConsentRequests.get(0);
//            newConsent.setEndTime(consentMessage.getEndTime());
//            sentConsentRequestRepository.save(newConsent);

            SentConsentRequest newConsent = SentConsentRequest.builder()
                    .approved(consentMessage.isApproved())
                    .patientId(consentMessage.getPatientId())
                    .destinationHospitalId(consentMessage.getDestinationHospitalId())
                    .sourceHospitalId(consentMessage.getSourceHospitalId())
                    .endTime(endTime)
                    .department(department)
                    .build();

            sentConsentRequestRepository.save(newConsent);
        }
        return true;
    }


    public Boolean requestData(RequestDataObject requestDataObject) {
        String recordSenderHospital = requestDataObject.getRecordSenderHospital();
        String patientId = requestDataObject.getPatientId();
        LocalDateTime requestTime = requestDataObject.getRequestTime();
        String department = requestDataObject.getDepartment();

        System.out.println("HELLO1");

        //fetch all consents for this patient with the destination hospital that are active now.
        List<SentConsentRequest> sentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(patientId,
                                true,
                                recordSenderHospital,
                                department);

        if(sentConsentRequests.size() == 0) {
            System.out.println("HELLO2");
            // TODO: found no consents, consent has to go to patient for approval
            return false;
        }
        else {
            // only one consent object found -> sentConsentRequests.size() == 1
            // update this consent object, with the end time to be the max of both times
            if(sentConsentRequests.get(0).getEndTime().isAfter(requestTime)) {
                rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_DATA_REQUEST_ROUTING_KEY, requestDataObject);
            }
            else {
                System.out.println("HELLO3");
                return false;
            }
            return true;
        }
    }

    public List<SentConsentRequest> getPendingConsents() {
        return sentConsentRequestRepository.findByApproved(false);
    }

    public List<SentConsentRequest> getApprovedConsents() {
        return sentConsentRequestRepository.findByApproved(true);
    }

    // This function sends the consent message to the consent manager queue
    public void sendDataToConsentManager(RequestDataObject requestDataObject) {
        // Do some checks
        // TODO

        List<PatientRecord> patientRecordList;
        patientRecordList = patientRecordRepository.getPatientRecordsByAadharAndDepartment(requestDataObject.getPatientId(), requestDataObject.getDepartment());

        for(PatientRecord patientRecord : patientRecordList) {
            DataMessage dataMessage = DataMessage.builder()
                    .sourceId(requestDataObject.getRecordSenderHospital())
                    .destinationId(requestDataObject.getRecordRequesterHospital())
                    .department(patientRecord.getDepartment())
                    .diagnosis(patientRecord.getDiagnosis())
                    .prescription(patientRecord.getPrescription())
                    .address(patientRecord.getAddress())
                    .aadhar(patientRecord.getAadhar())
                    .build();

            // If it passes all checks, send the data message to the consent manager
            rabbitTemplate.convertAndSend(
                    CM_EXCHANGE,
                    CM_DATA_ROUTING_KEY,
                    dataMessage
            );
        }
        System.out.println(patientRecordList.size() + " records Sent To Consent Manager!!!");

    }

    public List<ReceivedRecords> getRecords() {
        return receivedRecordsRepository.findAll();
    }

    public void clearRecords() {
        receivedRecordsRepository.deleteAll();
    }

    private Boolean isConsentValid(ConsentMessage consentMessage) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validFrom = consentMessage.getStartTime();
        LocalDateTime validTill = consentMessage.getEndTime();
        return validTill.isAfter(validFrom) &&
                validTill.isAfter(now) &&
                validFrom.isBefore(now);
    }

    public void addReceivedRecord(DataMessage dataMessage) {
        ReceivedRecords rc = ReceivedRecords.builder()
                .hospitalName(dataMessage.getSourceId())
                .aadhar(dataMessage.getAadhar())
                .prescription(dataMessage.getPrescription())
                .diagnosis(dataMessage.getDiagnosis())
                .department(dataMessage.getDepartment())
                .address(dataMessage.getAddress())
                .build();

        receivedRecordsRepository.save(rc);
    }
}
