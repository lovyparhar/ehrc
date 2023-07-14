package com.had.backend.patient.messageConsumer;

import com.had.backend.patient.config.MessagingConfig;
import com.had.backend.patient.entity.ConsentRequest;
import com.had.backend.patient.entity.User;
import com.had.backend.patient.model.ConsentMessage;
import com.had.backend.patient.repository.ConsentRequestRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RequestMessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMessageConsumer.class);

    @Autowired
    private ConsentRequestRepository consentRequestRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Whenever a consent request is received at the queue, store it in the database and push to front-end
    @RabbitListener(queues = {MessagingConfig.RECEIVE_REQUEST_QUEUE})
    public void consumeRequestMessage(ConsentMessage consentMessage) {

        // Displaying the message
        LOGGER.info(String.format("Received Consent Request message -> %s", consentMessage.toString()));

        List<ConsentRequest> unapprovedSentConsents =
                consentRequestRepository
                        .findByPatientIdAndDestinationHospitalIdAndSourceHospitalIdAndDepartmentAndApproved(
                                consentMessage.getPatientId(),
                                consentMessage.getDestinationHospitalId(),
                                consentMessage.getSourceHospitalId(),
                                consentMessage.getDepartment(),
                                false);

        for(ConsentRequest s: unapprovedSentConsents) {
            consentRequestRepository.deleteByIdPC(s.getIdPC());
        }

        ConsentRequest consentRequest =
                ConsentRequest.builder()
                .idCM(consentMessage.getIdCM())
                .idPC(consentMessage.getIdPC())
                .idDC(consentMessage.getIdDC())
                .approved(consentMessage.isApproved())
                .patientId(consentMessage.getPatientId())
                .sourceHospitalId(consentMessage.getSourceHospitalId())
                .destinationHospitalId(consentMessage.getDestinationHospitalId())
                .startTime(consentMessage.getStartTime())
                .endTime(consentMessage.getEndTime())
                .department(consentMessage.getDepartment())
                .doctorId(consentMessage.getDoctorId())
                .build();

        consentRequestRepository.save(consentRequest);
        simpMessagingTemplate.convertAndSendToUser(consentRequest.getPatientId(), "/consent-request", consentRequest);
    }
}