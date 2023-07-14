package com.had.backend.hospital.service;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.entity.*;
import com.had.backend.hospital.model.*;
import com.had.backend.hospital.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ConsentService {
    public static final String CM_EXCHANGE= "C_exchange";
    public static final String CM_HOSPITAL_ROUTING_KEY = "C_hospitalRoutingKey";
    public static final String CM_DATA_REQUEST_ROUTING_KEY = "C_dataRequestRoutingKey";
    public static final String CM_EMERGENCY_DATA_REQUEST_ROUTING_KEY = "C_emergencyDataRequestRoutingKey";
    public static final String CM_DATA_ROUTING_KEY = "C_dataRoutingKey";
    public static final String CM_OTP_CONSENT_ROUTING_KEY = "C_otpConsentRoutingKey";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SentConsentRequestRepository sentConsentRequestRepository;

    @Autowired
    private PatientRecordRepository patientRecordRepository;

    @Autowired
    private EmergencyDataRequestRepository emergencyDataRequestRepository;

    public Boolean requestConsent(RequestConsentObject requestConsentObject) {
        // Constraint: AtMAX 1 Approved Existing consent, AtMax 1 unapproved consent per tuple
        String recordSenderHospital = requestConsentObject.getRecordSenderHospital();
        String recordRequesterHospital = requestConsentObject.getRecordRequesterHospital();
        String patientId = requestConsentObject.getPatientId();
        LocalDateTime endTime = requestConsentObject.getEndTime();
        String department = requestConsentObject.getDepartment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        requestConsentObject.setDoctorId(user.getAadhar());


        // CLEARING OLD CONSENTS
        // fetch all consents for this patient with the destination hospital that are active now.
        List<SentConsentRequest> sentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(patientId,
                                true,
                                recordSenderHospital,
                                department);

        System.out.println("sentConsentRequests = " + sentConsentRequests);
        for(SentConsentRequest s: sentConsentRequests) {
            LocalDateTime end = s.getEndTime();
            LocalDateTime now = LocalDateTime.now();
            if (end.isBefore(now)) {
                sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
            }
        }


        // GETTING UNAPPROVED CONSENTS CORRESPONDING TO THIS TUPLE AND CLEARING THEM
        List<SentConsentRequest> unapprovedSentConsents =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(patientId,
                                false,
                                recordSenderHospital,
                                department);

        for(SentConsentRequest s: unapprovedSentConsents) {
            sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
        }

        SentConsentRequest sentConsentRequest = SentConsentRequest.builder()
                .approved(false)
                .patientId(patientId)
                .sourceHospitalId(recordSenderHospital)
                .destinationHospitalId(recordRequesterHospital)
                .endTime(endTime)
                .startTime(LocalDateTime.now())
                .department(department)
                .doctorId(user.getAadhar())
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
                .doctorId(user.getAadhar())
                .build();

        rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_HOSPITAL_ROUTING_KEY, toSend);
        System.out.println("Consent Request routed to CM for patient approval: " + toSend.toString());
        return false;
    }

    public Boolean updateConsent(ConsentMessage consentMessage) {

        // Delete all the unapproved consents
        List<SentConsentRequest> unapprovedSentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(
                                consentMessage.getPatientId(),
                                false,
                                consentMessage.getSourceHospitalId(),
                                consentMessage.getDepartment());

        for(SentConsentRequest s: unapprovedSentConsentRequests) {
            sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
        }

        // Delete all the approved consents
        List<SentConsentRequest> approvedSentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(
                                consentMessage.getPatientId(),
                                true,
                                consentMessage.getSourceHospitalId(),
                                consentMessage.getDepartment());

        for(SentConsentRequest s: approvedSentConsentRequests) {
            sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
        }

        SentConsentRequest newConsent = SentConsentRequest.builder()
                .approved(consentMessage.isApproved())
                .patientId(consentMessage.getPatientId())
                .destinationHospitalId(consentMessage.getDestinationHospitalId())
                .sourceHospitalId(consentMessage.getSourceHospitalId())
                .endTime(consentMessage.getEndTime())
                .department(consentMessage.getDepartment())
                .doctorId(consentMessage.getDoctorId())
                .build();

        sentConsentRequestRepository.save(newConsent);
        return true;
    }

    public Boolean requestData(RequestDataObject requestDataObject) {
        String recordSenderHospital = requestDataObject.getRecordSenderHospital();
        String patientId = requestDataObject.getPatientId();
        LocalDateTime requestTime = requestDataObject.getRequestTime();
        String department = requestDataObject.getDepartment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        requestDataObject.setDoctorId(user.getAadhar());

        System.out.println("requestDataObject = " + requestDataObject);

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

    public void requestEmergencyData(RequestDataObject requestDataObject) {
        String recordSenderHospital = requestDataObject.getRecordSenderHospital();
        String patientId = requestDataObject.getPatientId();
        LocalDateTime requestTime = requestDataObject.getRequestTime();
        String department = requestDataObject.getDepartment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        requestDataObject.setDoctorId(user.getAadhar());

        // Save the emergency data request in this hospital app
        EmergencyDataRequest em = EmergencyDataRequest.builder()
                .recordRequesterHospital(requestDataObject.getRecordRequesterHospital())
                .recordSenderHospital(requestDataObject.getRecordSenderHospital())
                .patientId(requestDataObject.getPatientId())
                .requestTime(requestDataObject.getRequestTime())
                .department(requestDataObject.getDepartment())
                .doctorId(user.getAadhar())
                .build();

        this.emergencyDataRequestRepository.save(em);

        // Save the emergency dta request in the consent manager too
        rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_EMERGENCY_DATA_REQUEST_ROUTING_KEY, requestDataObject);

        // Send the data request
        rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_DATA_REQUEST_ROUTING_KEY, requestDataObject);
    }

    public List<SentConsentRequest> getPendingConsents() {
        return sentConsentRequestRepository.findByApproved(false);
    }

    public List<SentConsentRequest> getApprovedConsents() {
        return sentConsentRequestRepository.findByApproved(true);
    }

    public List<SentConsentRequest> getExistingActiveConsents(ConsentMessage consentMessage) {
        List<SentConsentRequest> sentConsentRequests = sentConsentRequestRepository.findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(
                consentMessage.getPatientId(),
                consentMessage.getDestinationHospitalId(),
                consentMessage.getSourceHospitalId(),
                consentMessage.getDepartment(),
                true
        );

        System.out.println("sentConsentRequests = " + sentConsentRequests);

        List<SentConsentRequest> toReturn = new ArrayList<SentConsentRequest>();
        for(SentConsentRequest s: sentConsentRequests) {
            LocalDateTime endTime = s.getEndTime();
            LocalDateTime now = LocalDateTime.now();
            if(endTime.isAfter(now)) {
                toReturn.add(s);
            } else {
                sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
            }
        }
        System.out.println("toReturn = " + toReturn);
        return toReturn;
    }

    // This function sends the consent message to the consent manager queue
    public void sendDataToConsentManager(RequestDataObject requestDataObject) {
        List<PatientRecord> patientRecordList;
        patientRecordList = patientRecordRepository.getPatientRecordsByAadharAndDepartmentAndPrescriptionIsNotNullAndDiagnosisIsNotNull(requestDataObject.getPatientId(), requestDataObject.getDepartment());

        for(PatientRecord patientRecord : patientRecordList) {
            DataMessage dataMessage = DataMessage.builder()
                    .sourceId(requestDataObject.getRecordSenderHospital())
                    .destinationId(requestDataObject.getRecordRequesterHospital())
                    .department(patientRecord.getDepartment())
                    .diagnosis(patientRecord.getDiagnosis())
                    .prescription(patientRecord.getPrescription())
                    .address(patientRecord.getAddress())
                    .aadhar(patientRecord.getAadhar())
                    .patientLastName(patientRecord.getPatientLastName())
                    .patientFirstName(patientRecord.getPatientFirstName())
                    .doctorId(requestDataObject.getDoctorId())
                    .build();

            System.out.println("dataMessage = " + dataMessage);
            rabbitTemplate.convertAndSend(
                    CM_EXCHANGE,
                    CM_DATA_ROUTING_KEY,
                    dataMessage
            );

//            DataMessageEncrypted dataMessageEncrypted = dataMessage.encrypt(dataMessage, requestDataObject.getRecordRequesterHospital());
//            System.out.println("dataMessageEncrypted = " + dataMessageEncrypted);
//            rabbitTemplate.convertAndSend(
//                    CM_EXCHANGE,
//                    CM_DATA_ROUTING_KEY,
//                    dataMessageEncrypted
//            );
        }
        System.out.println(patientRecordList.size() + " records Sent To Consent Manager!!!");

    }

    private Boolean isConsentValid(ConsentMessage consentMessage) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validFrom = consentMessage.getStartTime();
        LocalDateTime validTill = consentMessage.getEndTime();
        return validTill.isAfter(validFrom) &&
                validTill.isAfter(now) &&
                validFrom.isBefore(now);
    }

    public void revokeConsent(ConsentMessage consentMessage) {
        List<SentConsentRequest> consentRequests = sentConsentRequestRepository.findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(
                consentMessage.getPatientId(),
                consentMessage.getDestinationHospitalId(),
                consentMessage.getSourceHospitalId(),
                consentMessage.getDepartment(),
                true
        );

        if(consentRequests.isEmpty()) return;

        SentConsentRequest consentRequest = consentRequests.get(0);
        System.out.println("consentRequest = " + consentRequest);
        sentConsentRequestRepository.deleteByIdDC(consentRequest.getIdDC());
    }

    public void approveConsentByOTP(RequestConsentObject requestConsentObject) {
        String patientId = requestConsentObject.getPatientId();
        LocalDateTime endTime = requestConsentObject.getEndTime();
        String department = requestConsentObject.getDepartment();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        requestConsentObject.setDoctorId(user.getAadhar());

        // Delete all the unapproved consents
        List<SentConsentRequest> unapprovedSentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(
                                patientId,
                                false,
                                requestConsentObject.getRecordSenderHospital(),
                                department);

        for(SentConsentRequest s: unapprovedSentConsentRequests) {
            sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
        }

        // Delete all the approved consents
        List<SentConsentRequest> approvedSentConsentRequests =
                sentConsentRequestRepository
                        .findSentConsentRequestByPatientIdAndApprovedAndSourceHospitalIdAndDepartment(
                                patientId,
                                true,
                                requestConsentObject.getRecordSenderHospital(),
                                department);

        for(SentConsentRequest s: approvedSentConsentRequests) {
            sentConsentRequestRepository.deleteByIdDC(s.getIdDC());
        }

        // Add the new approved consent
        SentConsentRequest newConsent = SentConsentRequest.builder()
                .approved(true)
                .patientId(requestConsentObject.getPatientId())
                .destinationHospitalId(requestConsentObject.getRecordRequesterHospital())
                .sourceHospitalId(requestConsentObject.getRecordSenderHospital())
                .endTime(endTime)
                .department(department)
                .doctorId(requestConsentObject.getDoctorId())
                .build();

        SentConsentRequest savedRequest = sentConsentRequestRepository.saveAndFlush(newConsent);

        ConsentMessage consentMessage = ConsentMessage.builder()
                .approved(true)
                .idDC(savedRequest.getIdDC())
                .destinationHospitalId(savedRequest.getDestinationHospitalId())
                .sourceHospitalId(savedRequest.getSourceHospitalId())
                .department(savedRequest.getDepartment())
                .endTime(savedRequest.getEndTime())
                .startTime(savedRequest.getStartTime())
                .patientId(savedRequest.getPatientId())
                .doctorId(savedRequest.getDoctorId())
                .build();

        rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_OTP_CONSENT_ROUTING_KEY, consentMessage);
    }
}
