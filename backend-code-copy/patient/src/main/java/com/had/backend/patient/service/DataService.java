package com.had.backend.patient.service;

import com.had.backend.patient.model.DataMessage;
import com.had.backend.patient.model.RequestDataObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataService {

    public static final String CM_EXCHANGE= "C_exchange";
    public static final String CM_HOSPITAL_ROUTING_KEY = "C_hospitalRoutingKey";
    public static final String CM_DATA_REQUEST_ROUTING_KEY = "C_dataRequestRoutingKey";
    public static final String DM_EXCHANGE= "D_exchange";
    public static final String DM_PATIENT_ROUTING_KEY = "D_dataRoutingKey";
    public static final String CM_DATA_ROUTING_KEY = "C_dataRoutingKey";

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void sendDataToFrontEnd(DataMessage sampleMessage) {
        System.out.println("HELLO");
        this.messagingTemplate.convertAndSend("/sendData", sampleMessage);
    }

    public Boolean requestData(RequestDataObject requestDataObject) {
        String recordSenderHospital = requestDataObject.getRecordSenderHospital();
        String patientId = requestDataObject.getPatientId();
        LocalDateTime requestTime = requestDataObject.getRequestTime();
        String department = requestDataObject.getDepartment();
        requestDataObject.setRecordRequesterHospital("P");
        System.out.println("requestDataObject = " + requestDataObject);
        System.out.println("Forwarding Patient Data Request To CM Data Request Queue: " + requestDataObject);

        try {
            rabbitTemplate.convertAndSend(CM_EXCHANGE, CM_DATA_REQUEST_ROUTING_KEY, requestDataObject);
            return true;
        } catch (Exception exception) {
            System.out.println("exception = " + exception);
            return false;
        }

    }
}
