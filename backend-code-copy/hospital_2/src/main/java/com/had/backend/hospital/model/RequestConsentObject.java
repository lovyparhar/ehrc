package com.had.backend.hospital.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestConsentObject {
    private String recordSenderHospital; //we need to get records from this hospital to us!
    private String recordRequesterHospital;
    private String patientId;
    private LocalDateTime endTime;
    private String department;
}
