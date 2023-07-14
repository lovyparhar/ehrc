package com.had.backend.patient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class updatePasswordRequest {
    private String OTP;
    private String phoneNumber;
    private String password;
    private String aadhar;

    public updatePasswordRequest(@JsonProperty("otp") String OTP,
                                 @JsonProperty("phoneNumber") String phoneNumber,
                                 @JsonProperty("password") String password,
                                 @JsonProperty("aadhar") String aadhar) {
        this.phoneNumber = phoneNumber;
        this.OTP = OTP;
        this.password = password;
        this.aadhar = aadhar;
    }
}
