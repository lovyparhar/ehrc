package com.had.backend.consentManager.messageConsumer;

import com.had.backend.consentManager.model.DataMessage;
import com.had.backend.consentManager.model.DataMessageEncrypted;
import com.had.backend.consentManager.service.ConsentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataMessageConsumer {
    @Autowired
    private ConsentService consentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMessageConsumer.class);

//    @RabbitListener(queues = {"C_dataQueue"})
//    public void consumeDataMessage(DataMessageEncrypted dataMessageEncrypted){
//        System.out.println(11111);
//        DataMessage dataMessage = dataMessageEncrypted.decryptRecord();
//        LOGGER.info(String.format("Received message -> %s", dataMessage.toString()));
//        consentService.forwardData(dataMessageEncrypted);
//        System.out.println("Records Forwared!!!!!");
//    }

    @RabbitListener(queues = {"C_dataQueue"})
    public void consumeDataMessage(DataMessage dataMessage){
        System.out.println(22222);
//        DataMessage dataMessage = dataMessageEncrypted.decryptRecord();
        LOGGER.info(String.format("Received message -> %s", dataMessage.toString()));
        consentService.forwardData(dataMessage);
        System.out.println("Records Forwared!!!!!");
    }
}