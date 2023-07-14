package com.had.backend.patient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class verifyOTPRequest {
    private String OTP;
    private String phoneNumber;
    private String aadhar;
}
