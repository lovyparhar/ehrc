package com.had.backend.hospital.messageConsumer;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.model.DataMessage;
import com.had.backend.hospital.service.ConsentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataMessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMessageConsumer.class);

    @Autowired
    private ConsentService consentService;

    @RabbitListener(queues = {MessagingConfig.RECEIVE_DATA_QUEUE})
    public void consumeDataMessage(DataMessage dataMessage){
        consentService.addReceivedRecord(dataMessage);
        LOGGER.info(String.format("Received Data message -> %s", dataMessage.toString()));
    }
}
