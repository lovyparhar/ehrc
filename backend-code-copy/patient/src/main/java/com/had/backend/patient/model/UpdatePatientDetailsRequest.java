package com.had.backend.patient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePatientDetailsRequest {

    private String email;
    private String phoneNumber;

    private String aadhar;
}
