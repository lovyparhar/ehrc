package com.had.backend.patient.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataMessage {
    private String sourceId;
    private String destinationId;
    private String department;
    private String address;
    private String diagnosis;
    private String prescription;
    private String aadhar;
    private String doctorId;
    private String patientFirstName;
    private String patientLastName;
}
