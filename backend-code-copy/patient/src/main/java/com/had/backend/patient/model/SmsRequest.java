package com.had.backend.patient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SmsRequest {
    private String phoneNumber;
    private String aadhar;


    public SmsRequest(@JsonProperty("phoneNumber") String phoneNumber, @JsonProperty("aadhar") String aadhar) {
        this.phoneNumber = phoneNumber;
        this.aadhar = aadhar;
    }
}
