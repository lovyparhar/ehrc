package com.had.backend.patient.config;

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
    public static final String RECEIVE_REQUEST_QUEUE = "P_receiveRequest";
    public static final String RECEIVE_DATA_QUEUE = "P_receiveData";
    public static final String OTP_CONSENT_QUEUE = "P_otpConsentQueue";
    public static final String EXCHANGE = "P_exchange";
    public static final String RECEIVE_REQUEST_ROUTING_KEY = "P_receiveRequestRoutingKey";
    public static final String RECEIVE_DATA_ROUTING_KEY = "P_receiveDataRoutingKey";
    public static final String OTP_CONSENT_ROUTING_KEY = "P_otpConsentRoutingKey";

    @Bean
    public Queue receiveRequestQueue(){
        return new Queue(RECEIVE_REQUEST_QUEUE);
    }

    @Bean
    public Queue receiveDataQueue(){
        return new Queue(RECEIVE_DATA_QUEUE);
    }

    @Bean
    public Queue otpConsentQueue(){
        return new Queue(OTP_CONSENT_QUEUE);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding receiveRequestBinding(){
        return BindingBuilder
                .bind(receiveRequestQueue())
                .to(exchange())
                .with(RECEIVE_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding receiveDataBinding(){
        return BindingBuilder
                .bind(receiveDataQueue())
                .to(exchange())
                .with(RECEIVE_DATA_ROUTING_KEY);
    }

    @Bean
    public Binding otpConsentBinding(){
        return BindingBuilder
                .bind(otpConsentQueue())
                .to(exchange())
                .with(OTP_CONSENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter(){
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }
}
