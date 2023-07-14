package com.had.backend.consentManager.config;

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
    public final String PATIENT_QUEUE = "C_patientQueue";
    public final String OTP_CONSENT_QUEUE = "C_otpConsentQueue";
    public final String REVOKE_CONSENT_QUEUE = "C_revokeConsentQueue";
    public final String HOSPITAL_QUEUE = "C_hospitalQueue";
    public final String DATA_REQUEST_QUEUE = "C_dataRequestQueue";
    public final String EMERGENCY_DATA_REQUEST_QUEUE = "C_emergencyDataRequestQueue";
    public final String DATA_QUEUE = "C_dataQueue";
    public final String EXCHANGE = "C_exchange";
    public final String PATIENT_ROUTING_KEY = "C_patientRoutingKey";
    public final String OTP_CONSENT_ROUTING_KEY = "C_otpConsentRoutingKey";
    public final String REVOKE_CONSENT_ROUTING_KEY = "C_revokeConsentRoutingKey";
    public final String HOSPITAL_ROUTING_KEY = "C_hospitalRoutingKey";
    public final String DATA_REQUEST_ROUTING_KEY = "C_dataRequestRoutingKey";
    public final String EMERGENCY_DATA_REQUEST_ROUTING_KEY = "C_emergencyDataRequestRoutingKey";
    public final String DATA_ROUTING_KEY = "C_dataRoutingKey";

    @Bean
    public Queue patientQueue(){
        return new Queue(PATIENT_QUEUE);
    }

    @Bean
    public Queue otpConsentQueue(){
        return new Queue(OTP_CONSENT_QUEUE);
    }

    @Bean
    public Queue revokeConsentQueue(){
        return new Queue(REVOKE_CONSENT_QUEUE);
    }

    @Bean
    public Queue hospitalQueue(){
        return new Queue(HOSPITAL_QUEUE);
    }

    @Bean
    public Queue dataRequestQueue(){
        return new Queue(DATA_REQUEST_QUEUE);
    }

    @Bean
    public Queue emergencyDataRequestQueue(){
        return new Queue(EMERGENCY_DATA_REQUEST_QUEUE);
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
    public Binding otpConsentBinding(){
        return BindingBuilder
                .bind(otpConsentQueue())
                .to(exchange())
                .with(OTP_CONSENT_ROUTING_KEY);
    }

    @Bean
    public Binding revokeConsentBinding(){
        return BindingBuilder
                .bind(revokeConsentQueue())
                .to(exchange())
                .with(REVOKE_CONSENT_ROUTING_KEY);
    }

    @Bean
    public Binding hospitalBinding(){
        return BindingBuilder
                .bind(hospitalQueue())
                .to(exchange())
                .with(HOSPITAL_ROUTING_KEY);
    }

    @Bean
    public Binding dataRequestBinding(){
        return BindingBuilder
                .bind(dataRequestQueue())
                .to(exchange())
                .with(DATA_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding emergencyDataRequestBinding(){
        return BindingBuilder
                .bind(emergencyDataRequestQueue())
                .to(exchange())
                .with(EMERGENCY_DATA_REQUEST_ROUTING_KEY);
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
