package com.had.backend.dataManager.service;

import com.had.backend.dataManager.model.DataMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class dataService {
    public static final String DM_EXCHANGE= "D_exchange";
    public static final String DM_PATIENT_ROUTING_KEY = "D_patientRoutingKey";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // This function creates a consent request entity from message and save it to the database
    // It then forwards the message to the destination hospital

    public void forwardData(DataMessage dataMessage) {
        // Do some checks
        // TODO

        // Don't store it!
        // If it passes all checks, send the data message to the destination
        rabbitTemplate.convertAndSend(
                dataMessage.getDestinationId() + "_exchange",
                dataMessage.getDestinationId() + "_receiveDataRoutingKey",
                dataMessage
        );
    }
}
