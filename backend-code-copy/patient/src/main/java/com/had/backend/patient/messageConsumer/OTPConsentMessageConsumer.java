package com.had.backend.patient.messageConsumer;

import com.had.backend.patient.entity.ConsentRequest;
import com.had.backend.patient.model.ConsentMessage;
import com.had.backend.patient.repository.ConsentRequestRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class OTPConsentMessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OTPConsentMessageConsumer.class);

    @Autowired
    private ConsentRequestRepository consentRequestRepository;

    @RabbitListener(queues = {"P_otpConsentQueue"})
    public void consumeOTPConsentMessage(ConsentMessage consentMessage){
        LOGGER.info(String.format("Received message -> %s", consentMessage.toString()));
        saveOTPConsent(consentMessage);
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
            consentRequestRepository.deleteByIdPC(s.getIdPC());
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
    }
}
