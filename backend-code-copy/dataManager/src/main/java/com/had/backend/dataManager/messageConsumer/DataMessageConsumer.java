package com.had.backend.dataManager.messageConsumer;

import com.had.backend.dataManager.model.DataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataMessageConsumer {
    @Autowired
    private com.had.backend.dataManager.service.dataService dataService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataMessageConsumer.class);

    @RabbitListener(queues = {"D_dataQueue"})
    public void consumeDataMessage(DataMessage dataMessage){
        LOGGER.info(String.format("Received message -> %s", dataMessage.toString()));
        dataService.forwardData(dataMessage);
        System.out.println("Records Forwared!!!!!");
    }
}
