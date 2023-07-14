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
public class DataRequestMessageConsumer {
    @Autowired
    private ConsentService consentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRequestMessageConsumer.class);

    @RabbitListener(queues = {MessagingConfig.DATA_REQUEST_QUEUE})
    public void consumeRequestMessage(RequestDataObject requestDataObject){
        LOGGER.info(String.format("Received Data Request message -> %s", requestDataObject.toString()));
        consentService.sendDataToConsentManager(requestDataObject);
    }
}
