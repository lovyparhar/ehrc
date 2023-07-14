package com.had.backend.consentManager.service;

import com.had.backend.consentManager.entity.ConsentRequest;
import com.had.backend.consentManager.entity.EmergencyDataRequest;
import com.had.backend.consentManager.messageConsumer.PatientMessageConsumer;
import com.had.backend.consentManager.model.ConsentMessage;
import com.had.backend.consentManager.model.DataMessage;
import com.had.backend.consentManager.model.DataMessageEncrypted;
import com.had.backend.consentManager.model.RequestDataObject;
import com.had.backend.consentManager.repository.ConsentRequestRepository;
import com.had.backend.consentManager.repository.EmergencyDataRequestRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ConsentService {
    public static final String P_EXCHANGE = "P_exchange";
    public static final String P_OTP_CONSENT_ROUTING_KEY = "P_otpConsentRoutingKey";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConsentRequestRepository consentRequestRepository;

    @Autowired
    private EmergencyDataRequestRepository emergencyDataRequestRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientMessageConsumer.class);

    // This function creates a consent request entity from message and save it to the database
    // It then forwards the message to the destination hospital
    public void saveAndForwardHospitalConsentRequest(ConsentMessage consentMessage) {
        // Store the consent in the consent table
        ConsentRequest consentRequest =
                ConsentRequest.builder()
                        .department(consentMessage.getDepartment())
                        .idPC(consentMessage.getIdPC())
                        .idDC(consentMessage.getIdDC())
                        .idSC(consentMessage.getIdSC())
                        .approved(consentMessage.isApproved())
                        .patientId(consentMessage.getPatientId())
                        .sourceHospitalId(consentMessage.getSourceHospitalId())
                        .destinationHospitalId(consentMessage.getDestinationHospitalId())
                        .startTime(consentMessage.getStartTime())
                        .endTime(consentMessage.getEndTime())
                        .doctorId(consentMessage.getDoctorId())
                        .build();

        LOGGER.info("Forwarding consent request to patient for approval");
        rabbitTemplate.convertAndSend(
                "P_exchange",
                "P_receiveRequestRoutingKey",
                consentMessage
        );
    }

    public void saveAndForwardPatientConsentRequest(ConsentMessage consentMessage) {
        ConsentRequest consentRequest =
                ConsentRequest.builder()
                        .department(consentMessage.getDepartment())
                        .idPC(consentMessage.getIdPC())
                        .idDC(consentMessage.getIdDC())
                        .idSC(consentMessage.getIdSC())
                        .approved(consentMessage.isApproved())
                        .patientId(consentMessage.getPatientId())
                        .sourceHospitalId(consentMessage.getSourceHospitalId())
                        .destinationHospitalId(consentMessage.getDestinationHospitalId())
                        .startTime(consentMessage.getStartTime())
                        .endTime(consentMessage.getEndTime())
                        .doctorId(consentMessage.getDoctorId())
                        .build();

        // If the consent came from the patient
        LOGGER.info(String.format("Forwarding consent request to the requester -> %s", consentRequest.getDestinationHospitalId()));

        List<ConsentRequest> consentRequests =
                consentRequestRepository
                        .findSentConsentRequestByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartment(
                                consentMessage.getPatientId(),
                                consentMessage.getDestinationHospitalId(),
                                consentMessage.getSourceHospitalId(),
                                consentMessage.getDepartment());

        for(ConsentRequest s: consentRequests) {
            consentRequestRepository.deleteByIdCM(s.getIdCM());
        }

        // saving the consent request in logs
        ConsentRequest savedRequest = consentRequestRepository.saveAndFlush(consentRequest);
        consentMessage.setIdCM(savedRequest.getIdCM());

        rabbitTemplate.convertAndSend(
                consentMessage.getDestinationHospitalId() + "_exchange",
                consentMessage.getDestinationHospitalId() + "_receiveApprovedRequestRoutingKey",
                consentMessage
        );
    }

    public void saveAndForwardDataRequest(RequestDataObject requestDataObject) {

        LOGGER.info(String.format("Forwarding data request to record sender -> %s", requestDataObject.getRecordSenderHospital()));

        rabbitTemplate.convertAndSend(
                requestDataObject.getRecordSenderHospital() + "_exchange",
                requestDataObject.getRecordSenderHospital() + "_requestDataRoutingKey",
                requestDataObject
        );
    }

    public void saveEmergencyDataRequest(RequestDataObject requestDataObject) {

        EmergencyDataRequest em = EmergencyDataRequest.builder()
                .recordRequesterHospital(requestDataObject.getRecordRequesterHospital())
                .recordSenderHospital(requestDataObject.getRecordSenderHospital())
                .patientId(requestDataObject.getPatientId())
                .requestTime(requestDataObject.getRequestTime())
                .department(requestDataObject.getDepartment())
                .doctorId(requestDataObject.getDoctorId())
                .build();

        this.emergencyDataRequestRepository.save(em);

    }

    public void forwardData(DataMessage dataMessage) {
        rabbitTemplate.convertAndSend(
                dataMessage.getDestinationId() + "_exchange",
                dataMessage.getDestinationId() + "_receiveDataRoutingKey",
                dataMessage
        );
    }
    public void forwardData(DataMessageEncrypted dataMessageEncrypted) {
        rabbitTemplate.convertAndSend(
                dataMessageEncrypted.getDestinationId() + "_exchange",
                dataMessageEncrypted.getDestinationId() + "_receiveDataRoutingKey",
                dataMessageEncrypted
        );
    }

    public void revokeConsent(ConsentMessage consentMessage) {
        List<ConsentRequest> consentRequests = consentRequestRepository.findSentConsentRequestByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartment(
                consentMessage.getPatientId(),
                consentMessage.getDestinationHospitalId(),
                consentMessage.getSourceHospitalId(),
                consentMessage.getDepartment()
        );

        if(consentRequests.isEmpty()) return;

        ConsentRequest consentRequest = consentRequests.get(0);
        consentRequestRepository.deleteByIdCM(consentRequest.getIdCM());

        rabbitTemplate.convertAndSend(
                consentMessage.getDestinationHospitalId() + "_exchange",
                consentMessage.getDestinationHospitalId() + "_revokeConsentRoutingKey",
                consentMessage
        );
    }

    public void saveOTPConsent(ConsentMessage consentMessage) {

        // Remove the earlier consents
        List<ConsentRequest> consentRequests =
                consentRequestRepository
                        .findSentConsentRequestByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartment(
                                consentMessage.getPatientId(),
                                consentMessage.getDestinationHospitalId(),
                                consentMessage.getSourceHospitalId(),
                                consentMessage.getDepartment());

        for(ConsentRequest s: consentRequests) {
            consentRequestRepository.deleteByIdCM(s.getIdCM());
        }

        // Store the consent in the consent table
        ConsentRequest consentRequest =
                ConsentRequest.builder()
                        .department(consentMessage.getDepartment())
                        .idPC(consentMessage.getIdPC())
                        .idDC(consentMessage.getIdDC())
                        .idSC(consentMessage.getIdSC())
                        .approved(consentMessage.isApproved())
                        .patientId(consentMessage.getPatientId())
                        .sourceHospitalId(consentMessage.getSourceHospitalId())
                        .destinationHospitalId(consentMessage.getDestinationHospitalId())
                        .startTime(consentMessage.getStartTime())
                        .endTime(consentMessage.getEndTime())
                        .doctorId(consentMessage.getDoctorId())
                        .build();

        // saving the consent request in logs
        consentRequestRepository.save(consentRequest);
        rabbitTemplate.convertAndSend(P_EXCHANGE, P_OTP_CONSENT_ROUTING_KEY, consentMessage);
    }
}
