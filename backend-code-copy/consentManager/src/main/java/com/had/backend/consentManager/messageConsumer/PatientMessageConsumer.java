package com.had.backend.consentManager.messageConsumer;

import com.had.backend.consentManager.model.ConsentMessage;
import com.had.backend.consentManager.model.DataMessage;
import com.had.backend.consentManager.repository.ConsentRequestRepository;
import com.had.backend.consentManager.service.ConsentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientMessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientMessageConsumer.class);

    @Autowired
    private ConsentService consentService;

    // Listens to the requests from the patient queue
    @RabbitListener(queues = {"C_patientQueue"})
    public void consumePatientMessage(ConsentMessage consentMessage){
        LOGGER.info(String.format("Received message -> %s", consentMessage.toString()));
        consentService.saveAndForwardPatientConsentRequest(consentMessage);
    }
}
