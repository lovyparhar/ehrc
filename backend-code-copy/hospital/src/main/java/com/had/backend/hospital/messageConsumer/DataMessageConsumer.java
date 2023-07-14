package com.had.backend.hospital.messageConsumer;

import com.had.backend.hospital.config.MessagingConfig;
import com.had.backend.hospital.model.DataMessage;
import com.had.backend.hospital.model.DataMessageEncrypted;
import com.had.backend.hospital.service.ConsentService;
import com.had.backend.hospital.service.RecordServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataMessageConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMessageConsumer.class);

    @Autowired
    private RecordServices  recordServices;

//    @RabbitListener(queues = {MessagingConfig.RECEIVE_DATA_QUEUE})
//    public void consumeDataMessage(DataMessageEncrypted dataMessageEncrypted){
//        DataMessage dataMessage = dataMessageEncrypted.decryptRecord();
//        recordServices.addReceivedRecord(dataMessage);
//        LOGGER.info(String.format("Received Data message -> %s", dataMessage.toString()));
//    }

    @RabbitListener(queues = {MessagingConfig.RECEIVE_DATA_QUEUE})
    public void consumeDataMessage(DataMessage dataMessage){
        recordServices.addReceivedRecord(dataMessage);
        LOGGER.info(String.format("Received Data message -> %s", dataMessage.toString()));
    }

}
