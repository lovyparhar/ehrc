package com.had.backend.hospital.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
public class MessagingConfig {
    public static String hospitalId = "H1";
    public static final String RECEIVE_APPROVED_REQUEST_QUEUE = "H1_receiveApprovedRequest";
    public static final String RECEIVE_DATA_QUEUE = "H1_receiveData";
    public static final String DATA_REQUEST_QUEUE = "H1_requestData";
    public static final String REVOKE_CONSENT_QUEUE = "H1_revokeConsentQueue";
    public static final String EXCHANGE = "H1_exchange";

    public static final String RECEIVE_APPROVED_REQUEST_ROUTING_KEY = "H1_receiveApprovedRequestRoutingKey";
    public static final String RECEIVE_DATA_ROUTING_KEY = "H1_receiveDataRoutingKey";
    public static final String DATA_REQUEST_ROUTING_KEY = "H1_requestDataRoutingKey";
    public static final String REVOKE_CONSENT_ROUTING_KEY = "H1_revokeConsentRoutingKey";



    @Bean
    public Queue receiveApprovedRequestQueue(){
        return new Queue(RECEIVE_APPROVED_REQUEST_QUEUE);
    }

    @Bean
    public Queue revokeConsentQueue(){
        return new Queue(REVOKE_CONSENT_QUEUE);
    }

    @Bean
    public Queue receiveDataQueue(){
        return new Queue(RECEIVE_DATA_QUEUE);
    }

    @Bean
    public Queue dataRequestQueue() { return new Queue(DATA_REQUEST_QUEUE); }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding receiveApprovedRequestBinding(){
        return BindingBuilder
                .bind(receiveApprovedRequestQueue())
                .to(exchange())
                .with(RECEIVE_APPROVED_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding revokeConsentBinding(){
        return BindingBuilder
                .bind(revokeConsentQueue())
                .to(exchange())
                .with(REVOKE_CONSENT_ROUTING_KEY);
    }

    @Bean
    public Binding receiveDataBinding(){
        return BindingBuilder
                .bind(receiveDataQueue())
                .to(exchange())
                .with(RECEIVE_DATA_ROUTING_KEY);
    }

    @Bean
    public Binding dataRequestBinding(){
        return BindingBuilder
                .bind(dataRequestQueue())
                .to(exchange())
                .with(DATA_REQUEST_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter(){
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }
}
