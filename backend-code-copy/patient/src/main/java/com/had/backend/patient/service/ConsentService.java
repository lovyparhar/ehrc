package com.had.backend.patient.service;

import com.had.backend.patient.entity.ConsentRequest;
import com.had.backend.patient.entity.PatientRecord;
import com.had.backend.patient.entity.User;
import com.had.backend.patient.model.ConsentMessage;
import com.had.backend.patient.repository.ConsentRequestRepository;
import com.had.backend.patient.repository.PatientRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class ConsentService {
    public static final String CM_EXCHANGE= "C_exchange";
    public static final String CM_PATIENT_ROUTING_KEY = "C_patientRoutingKey";
    public static final String CM_REVOKE_CONSENT_ROUTING_KEY = "C_revokeConsentRoutingKey";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConsentRequestRepository consentRequestRepository;

    @Autowired
    private PatientRecordRepository patientRecordRepository;

    public Boolean sendConsent(ConsentMessage consentMessage) {
        String recordSenderHospital = consentMessage.getSourceHospitalId();
        String recordRequesterHospital = consentMessage.getDestinationHospitalId();
        String patientId = consentMessage.getPatientId();
        String department = consentMessage.getDepartment();
        LocalDateTime endTime = consentMessage.getEndTime();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        List<ConsentRequest> approvedConsentRequests = consentRequestRepository
            .findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(
                    consentMessage.getPatientId(),
                    consentMessage.getDestinationHospitalId(),
                    consentMessage.getSourceHospitalId(),
                    consentMessage.getDepartment(),
                    true
            );

        for(ConsentRequest c: approvedConsentRequests) {
            consentRequestRepository.deleteByIdPC(c.getIdPC());
        }

        ConsentRequest consentRequest =
                ConsentRequest.builder()
                        .idCM(consentMessage.getIdCM())
                        .idSC(consentMessage.getIdSC())
                        .idDC(consentMessage.getIdDC())
                        .approved(true) // has to be true
                        .patientId(user.getAadhar())
                        .sourceHospitalId(consentMessage.getSourceHospitalId())
                        .startTime(consentMessage.getStartTime())
                        .endTime(consentMessage.getEndTime())
                        .destinationHospitalId(consentMessage.getDestinationHospitalId())
                        .department(consentMessage.getDepartment())
                        .doctorId(consentMessage.getDoctorId())
                        .build();

        List<ConsentRequest> unapprovedConsentRequests = consentRequestRepository
                .findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(
                        consentMessage.getPatientId(),
                        consentMessage.getDestinationHospitalId(),
                        consentMessage.getSourceHospitalId(),
                        consentMessage.getDepartment(),
                        false
                );

        for(ConsentRequest c: unapprovedConsentRequests) {
            consentRequestRepository.deleteByIdPC(c.getIdPC());
        }

        ConsentRequest savedPatientConsentRequest = consentRequestRepository.saveAndFlush(consentRequest);
        consentMessage.setIdPC(savedPatientConsentRequest.getIdPC());

        rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_PATIENT_ROUTING_KEY, consentMessage);
        return true;
    }

    public List<ConsentRequest> getConsents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ConsentRequest> consentRequests = new ArrayList<>();
        User user = (User) auth.getPrincipal();
        consentRequests = consentRequestRepository.findByPatientId(user.getAadhar());
        return consentRequests;
    }

    public List<ConsentRequest> getPendingConsents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ConsentRequest> consentRequests = new ArrayList<>();
        User user = (User) auth.getPrincipal();
        consentRequests = consentRequestRepository.findByPatientIdAndApproved(user.getAadhar(), false);
        return consentRequests;
    }

    public List<ConsentRequest> getApprovedConsents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<ConsentRequest> consentRequests = consentRequestRepository.findByPatientIdAndApproved(user.getAadhar(), true);
        return consentRequests;
    }

    public List<ConsentRequest> getConsentsFromHospital(String hospitalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ConsentRequest> consentRequests = new ArrayList<>();
        consentRequests = consentRequestRepository.findBySourceHospitalId(hospitalId);
        return consentRequests;
    }

    public List<PatientRecord> getRecords() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        List<PatientRecord> patientRecordList;
        patientRecordList = patientRecordRepository.getPatientRecordsByAadhar(user.getAadhar());
        return patientRecordList;
    }

    public void clearRecords() {
        patientRecordRepository.deleteAll();
    }

    public Boolean deleteRecord(Long id) {
        try {
            consentRequestRepository.deleteById(id);
            return true;
        } catch (Exception exception) {
            System.out.println("exception = " + exception);
            return false;
        }
    }

    public void revokeConsent(String id) {
        ConsentRequest consentRequest = consentRequestRepository.getByIdPC(Long.parseLong(id));
        ConsentMessage consentMessage = ConsentMessage.builder()
                .idPC(consentRequest.getIdPC())
                .idCM(consentRequest.getIdCM())
                .idSC(consentRequest.getIdSC())
                .idDC(consentRequest.getIdDC())
                .patientId(consentRequest.getPatientId())
                .endTime(consentRequest.getEndTime())
                .startTime(consentRequest.getStartTime())
                .department(consentRequest.getDepartment())
                .destinationHospitalId(consentRequest.getDestinationHospitalId())
                .sourceHospitalId(consentRequest.getSourceHospitalId())
                .approved(consentRequest.isApproved())
                .doctorId(consentRequest.getDoctorId())
                .build();

        System.out.println("consentMessage = " + consentMessage);

        consentRequestRepository.deleteByIdPC(consentRequest.getIdPC());
        rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_REVOKE_CONSENT_ROUTING_KEY, consentMessage);
    }
}
