package com.had.backend.hospital.messageConsumer;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.model.ConsentMessage;
import com.had.backend.hospital.model.RequestDataObject;
import com.had.backend.hospital.service.ConsentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovedRequestMessageConsumer {
    @Autowired
    private ConsentService consentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApprovedRequestMessageConsumer.class);

    @RabbitListener(queues = {MessagingConfig.RECEIVE_APPROVED_REQUEST_QUEUE})
    public void consumeRequestMessage(ConsentMessage consentMessage){
        LOGGER.info(String.format("Received Request message -> %s", consentMessage.toString()));
        consentService.updateConsent(consentMessage);
//        consentService.sendDataToConsentManager(requestDataObject);
    }
}