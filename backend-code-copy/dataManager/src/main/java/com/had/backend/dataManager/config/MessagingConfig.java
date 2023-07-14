package com.had.backend.dataManager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
    public final String PATIENT_QUEUE = "D_patientQueue";
    public final String HOSPITAL_QUEUE = "D_hospitalQueue";
    public final String DATA_QUEUE = "D_dataQueue";
    public final String EXCHANGE = "D_exchange";
    public final String PATIENT_ROUTING_KEY = "D_patientRoutingKey";
    public final String HOSPITAL_ROUTING_KEY = "D_hospitalRoutingKey";
    public final String DATA_ROUTING_KEY = "D_dataRoutingKey";

    @Bean
    public Queue patientQueue(){
        return new Queue(PATIENT_QUEUE);
    }

    @Bean
    public Queue hospitalQueue(){
        return new Queue(HOSPITAL_QUEUE);
    }

    @Bean
    public Queue dataQueue(){
        return new Queue(DATA_QUEUE);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding patientBinding(){
        return BindingBuilder
                .bind(patientQueue())
                .to(exchange())
                .with(PATIENT_ROUTING_KEY);
    }

    @Bean
    public Binding hospitalBinding(){
        return BindingBuilder
                .bind(hospitalQueue())
                .to(exchange())
                .with(HOSPITAL_ROUTING_KEY);
    }

    @Bean
    public Binding dataBinding(){
        return BindingBuilder
                .bind(dataQueue())
                .to(exchange())
                .with(DATA_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter(){
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }
}
