package com.had.backend.consentManager.messageConsumer;

import com.had.backend.consentManager.model.ConsentMessage;
import com.had.backend.consentManager.service.ConsentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalMessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HospitalMessageConsumer.class);
    @Autowired
    private ConsentService consentService;

    @RabbitListener(queues = {"C_hospitalQueue"})
    public void consumeHospitalMessage(ConsentMessage consentMessage){
        LOGGER.info(String.format("Received message -> %s", consentMessage.toString()));
        consentService.saveAndForwardHospitalConsentRequest(consentMessage);
    }
}
