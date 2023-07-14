package com.had.backend.hospital.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddRecordRequest
{

    private String hospitalName;
    private String department;
    private String address;
    private String diagnosis;
    private String prescription;
    private String aadhar;
    private String doctorId;
    private String patientFirstName;
    private String patientLastName;

}
