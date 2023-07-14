package com.had.backend.consentManager.controller;

import com.had.backend.consentManager.model.DataMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/")
    public String hello() {
        return "Consent Manager is running";
    }
}
